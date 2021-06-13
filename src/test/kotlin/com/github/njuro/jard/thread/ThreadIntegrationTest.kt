package com.github.njuro.jard.thread

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.common.InputConstraints.MAX_NAME_LENGTH
import com.github.njuro.jard.common.InputConstraints.MAX_SUBJECT_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.common.WithMockUserAuthorities
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.post.dto.DeleteOwnPostDto
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.post.dto.PostForm
import com.github.njuro.jard.thread.dto.ThreadDto
import com.github.njuro.jard.thread.dto.ThreadForm
import com.github.njuro.jard.user.UserAuthority
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.util.*


@UseMockDatabase
@Transactional
internal class ThreadIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var board: Board

    @BeforeEach
    fun createBoard() {
        board = boardRepository.save(
            board(
                label = "r",
                settings = boardSettings(attachmentCategories = mutableSetOf(AttachmentCategory.IMAGE))
            )
        )
    }

    @Nested
    @DisplayName("create thread")
    inner class CreateThread {
        private fun createThread(
            threadForm: ThreadForm,
            attachment: MockMultipartFile? = multipartFile("attachment", TEST_ATTACHMENT_1)
        ) =
            mockMvc.multipart(Mappings.API_ROOT_THREADS, board.label) {
                part("threadForm", threadForm)
                if (attachment != null) file(attachment)
                with { it.apply { method = HttpMethod.PUT.name } }
            }

        @Test
        fun `create valid thread`() {
            val threadForm = thread(board, subject = "Test subject").toForm()
            createThread(threadForm).andExpect { status { isOk() } }
                .andReturnConverted<ThreadDto>().threadNumber shouldBe 1L
        }

        @Test
        fun `don't create invalid thread`() {
            val threadForm = thread(board, subject = randomString(MAX_SUBJECT_LENGTH + 1)).toForm()
            createThread(threadForm).andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("reply to thread")
    inner class ReplyToThread {
        private fun replyToThread(
            postForm: PostForm,
            thread: Thread,
            attachment: MockMultipartFile? = multipartFile("attachment", TEST_ATTACHMENT_1)
        ) =
            mockMvc.multipart(
                "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}",
                board.label,
                thread.threadNumber
            ) {
                part("postForm", postForm)
                if (attachment != null) file(attachment)
                with { it.apply { method = HttpMethod.PUT.name } }
            }


        @Test
        fun `create valid reply`() {
            val thread = saveThread(thread(board))
            val postForm = post(thread).toForm()

            replyToThread(postForm, thread).andExpect { status { isOk() } }
                .andReturnConverted<PostDto>().postNumber shouldBe 1L
        }

        @Test
        fun `don't create invalid reply`() {
            val thread = saveThread(thread(board))
            val postForm = post(thread, name = randomString(MAX_NAME_LENGTH + 1)).toForm()

            replyToThread(postForm, thread).andExpect { status { isBadRequest() } }
        }

    }

    @Nested
    @DisplayName("get thread")
    inner class GetThread {
        private fun getThread(threadNumber: Long) = mockMvc.get(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}",
            board.label,
            threadNumber
        ) { setUp() }


        @Test
        fun `get existing thread`() {
            val thread = saveThread(thread(board))
            getThread(thread.threadNumber).andExpect { status { isOk() } }
                .andReturnConverted<ThreadDto>().threadNumber shouldBe 1L
        }

        @Test
        fun `don't get non-existing thread`() {
            getThread(0L).andExpect { status { isNotFound() } }
        }
    }

    @Test
    fun `get new replies for thread`() {
        val thread = saveThread(thread(board))
        saveReply(post(thread, postNumber = 2L))
        saveReply(post(thread, postNumber = 3L))

        mockMvc.get(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/new-replies?lastPost=1",
            board.label,
            thread.threadNumber
        )
        { setUp() }.andExpect { status { isOk() } }.andReturnConverted<List<PostDto>>() shouldHaveSize 2
    }

    @Test
    @WithMockUserAuthorities(UserAuthority.TOGGLE_STICKY_THREAD)
    fun `toggle sticky on thread`() {
        val thread = saveThread(thread(board))

        thread.isStickied.shouldBeFalse()
        mockMvc.post(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/sticky",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }
        getUpdatedThread(thread).shouldBePresent { it.isStickied.shouldBeTrue() }
    }

    @Test
    @WithMockUserAuthorities(UserAuthority.TOGGLE_LOCK_THREAD)
    fun `toggle lock on thread`() {
        val thread = saveThread(thread(board))

        thread.isLocked.shouldBeFalse()
        mockMvc.post(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/lock",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }
        getUpdatedThread(thread).shouldBePresent { it.isLocked.shouldBeTrue() }
    }

    @Nested
    @DisplayName("delete post")
    @WithMockUserAuthorities(UserAuthority.DELETE_POST)
    inner class DeletePost {
        private fun deletePost(postNumber: Long, threadNumber: Long) = mockMvc.delete(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/${Mappings.PATH_VARIABLE_POST}",
            board.label,
            threadNumber,
            postNumber
        ) { setUp() }

        @Test
        fun `delete post`() {
            val thread = saveThread(thread(board))

            deletePost(thread.originalPost.postNumber, thread.threadNumber).andExpect { status { isOk() } }
            getUpdatedThread(thread).shouldBeEmpty()
        }
    }

    @Nested
    @DisplayName("delete own post")
    inner class DeleteOwnPost {
        private fun deleteOwnPost(postNumber: Long, threadNumber: Long, deletionCode: String) = mockMvc.delete(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/${Mappings.PATH_VARIABLE_POST}/delete-own",
            board.label,
            threadNumber,
            postNumber
        ) { body(DeleteOwnPostDto(deletionCode)) }

        @Test
        fun `delete own post`() {
            val thread = saveThread(thread(board))
            val reply = saveReply(post(thread, deletionCode = "abcde", postNumber = 2L))

            getUpdatedPost(reply).shouldBePresent()
            deleteOwnPost(reply.postNumber, thread.threadNumber, "abcde").andExpect { status { isOk() } }
            getUpdatedPost(reply).shouldBeEmpty()
        }

        @Test
        fun `don't delete own post with incorrect deletion code`() {
            val thread = saveThread(thread(board))
            val reply = saveReply(post(thread, deletionCode = "abcde", postNumber = 2L))

            getUpdatedPost(reply).shouldBePresent()
            deleteOwnPost(reply.postNumber, thread.threadNumber, "ghfij").andExpect { status { isBadRequest() } }
            getUpdatedPost(reply).shouldBePresent()
        }
    }

    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }

    private fun saveReply(post: Post): Post {
        return postRepository.save(post)
    }

    private fun getUpdatedThread(thread: Thread): Optional<Thread> {
        return threadRepository.findById(thread.id)
    }

    private fun getUpdatedPost(post: Post): Optional<Post> {
        return postRepository.findById(post.id)
    }
}