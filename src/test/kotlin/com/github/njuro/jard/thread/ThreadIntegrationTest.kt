package com.github.njuro.jard.thread

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.TEST_ATTACHMENT_PNG
import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.boardSettings
import com.github.njuro.jard.common.InputConstraints.MAX_NAME_LENGTH
import com.github.njuro.jard.common.InputConstraints.MAX_SUBJECT_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.multipartFile
import com.github.njuro.jard.post
import com.github.njuro.jard.post.dto.DeleteOwnPostDto
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.post.dto.PostForm
import com.github.njuro.jard.randomString
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.dto.ThreadDto
import com.github.njuro.jard.thread.dto.ThreadForm
import com.github.njuro.jard.toForm
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
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.patch
import org.springframework.transaction.annotation.Transactional

@WithContainerDatabase
@Transactional
internal class ThreadIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var db: TestDataRepository

    private lateinit var board: Board

    @BeforeEach
    fun createBoard() {
        board = db.insert(
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
            attachment: MockMultipartFile? = multipartFile("attachment", TEST_ATTACHMENT_PNG)
        ) =
            mockMvc.multipart(Mappings.API_ROOT_THREADS, board.label) {
                part("threadForm", threadForm)
                if (attachment != null) file(attachment)
            }

        @Test
        fun `create valid thread`() {
            val threadForm = thread(board, subject = "Test subject").toForm()
            createThread(threadForm).andExpect { status { isCreated() } }
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
            attachment: MockMultipartFile? = multipartFile("attachment", TEST_ATTACHMENT_PNG)
        ) =
            mockMvc.multipart(
                "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}",
                board.label,
                thread.threadNumber
            ) {
                part("postForm", postForm)
                if (attachment != null) file(attachment)
            }

        @Test
        fun `create valid reply`() {
            val thread = db.insert(thread(board))
            val postForm = post(thread).toForm()

            replyToThread(postForm, thread).andExpect { status { isCreated() } }
                .andReturnConverted<PostDto>().postNumber shouldBe 1L
        }

        @Test
        fun `don't create invalid reply`() {
            val thread = db.insert(thread(board))
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
            val thread = db.insert(thread(board))
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
        val thread = db.insert(thread(board))
        db.insert(post(thread, postNumber = 2L))
        db.insert(post(thread, postNumber = 3L))

        mockMvc.get(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/new-replies?lastPost=1",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }.andReturnConverted<List<PostDto>>() shouldHaveSize 2
    }

    @Test
    @WithMockJardUser(UserAuthority.TOGGLE_STICKY_THREAD)
    fun `toggle sticky on thread`() {
        val thread = db.insert(thread(board))

        thread.isStickied.shouldBeFalse()
        mockMvc.patch(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/sticky",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }
        db.select(thread).shouldBePresent { it.isStickied.shouldBeTrue() }
    }

    @Test
    @WithMockJardUser(UserAuthority.TOGGLE_LOCK_THREAD)
    fun `toggle lock on thread`() {
        val thread = db.insert(thread(board))

        thread.isLocked.shouldBeFalse()
        mockMvc.patch(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/lock",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }
        db.select(thread).shouldBePresent { it.isLocked.shouldBeTrue() }
    }

    @Nested
    @DisplayName("delete post")
    @WithMockJardUser(UserAuthority.DELETE_POST)
    inner class DeletePost {
        private fun deletePost(postNumber: Long, threadNumber: Long) = mockMvc.delete(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/${Mappings.PATH_VARIABLE_POST}",
            board.label,
            threadNumber,
            postNumber
        ) { setUp() }

        @Test
        fun `delete post`() {
            val thread = db.insert(thread(board))

            deletePost(thread.originalPost.postNumber, thread.threadNumber).andExpect { status { isOk() } }
            db.select(thread).shouldBeEmpty()
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
            val thread = db.insert(thread(board))
            val reply = db.insert(post(thread, deletionCode = "abcde", postNumber = 2L))

            db.select(reply).shouldBePresent()
            deleteOwnPost(reply.postNumber, thread.threadNumber, "abcde").andExpect { status { isOk() } }
            db.select(reply).shouldBeEmpty()
        }

        @Test
        fun `don't delete own post with incorrect deletion code`() {
            val thread = db.insert(thread(board))
            val reply = db.insert(post(thread, deletionCode = "abcde", postNumber = 2L))

            db.select(reply).shouldBePresent()
            deleteOwnPost(reply.postNumber, thread.threadNumber, "ghfij").andExpect { status { isBadRequest() } }
            db.select(reply).shouldBePresent()
        }
    }
}
