package com.github.njuro.jard.thread

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.TEST_ATTACHMENT_PNG
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.ban.UserBannedException
import com.github.njuro.jard.board
import com.github.njuro.jard.board.BoardFacade
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.common.InputConstraints.MAX_ATTACHMENT_SIZE
import com.github.njuro.jard.common.InputConstraints.MAX_NAME_LENGTH
import com.github.njuro.jard.common.InputConstraints.MAX_POST_LENGTH
import com.github.njuro.jard.common.InputConstraints.MAX_SUBJECT_LENGTH
import com.github.njuro.jard.common.InputConstraints.MAX_TRIPCODE_PASSWORD_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.multipartFile
import com.github.njuro.jard.post
import com.github.njuro.jard.post.PostFacade
import com.github.njuro.jard.post.dto.DeleteOwnPostDto
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.post.dto.PostForm
import com.github.njuro.jard.randomString
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.dto.ThreadDto
import com.github.njuro.jard.thread.dto.ThreadForm
import com.github.njuro.jard.toForm
import com.github.njuro.jard.user.UserAuthority
import com.github.njuro.jard.utils.HttpUtils
import com.github.njuro.jard.utils.validation.FormValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.patch
import java.io.IOException

@WithContainerDatabase
internal class ThreadControllerTest : MockMvcTest() {

    @MockkBean
    private lateinit var threadFacade: ThreadFacade

    @MockkBean
    private lateinit var boardFacade: BoardFacade

    @MockkBean
    private lateinit var postFacade: PostFacade

    private val board = board(label = "r")
    private val thread = thread(board)
    private val post = post(thread)

    @BeforeEach
    fun initMocks() {
        mockkStatic(HttpUtils::class)
        every { HttpUtils.getClientIp(any()) } returns "0.0.0.0"
        every { boardFacade.resolveBoard(board.label) } returns board.toDto()
        every { threadFacade.resolveThread(board.label, thread.threadNumber) } returns thread.toDto()
        every { postFacade.resolvePost(board.label, post.postNumber) } returns post.toDto()
    }

    @AfterEach
    fun clearMocks() {
        unmockkStatic(HttpUtils::class)
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
            val thread = thread(board, subject = "Test thread")
            val threadForm = slot<ThreadForm>()
            every { threadFacade.createThread(capture(threadForm), ofType(BoardDto::class)) } returns thread.toDto()

            val response =
                createThread(thread.toForm()).andExpect { status { isCreated() } }.andReturnConverted<ThreadDto>()
            response.threadNumber shouldBe thread.threadNumber
            threadForm.captured.should {
                it.postForm.ip shouldStartWith "0"
                it.postForm.attachment.originalFilename shouldBe TEST_ATTACHMENT_PNG
            }
        }

        @Test
        fun `create thread with embedded attachment in original post`() {
            val thread = thread(board, subject = "Test thread")
            every {
                threadFacade.createThread(
                    ofType(ThreadForm::class),
                    ofType(BoardDto::class)
                )
            } answers { thread.toDto() }

            createThread(
                thread.toForm().apply { postForm.embedUrl = "some_url" },
                attachment = null
            ).andExpect { status { isCreated() } }
                .andReturnConverted<ThreadDto>().shouldNotBeNull()
        }

        @Test
        fun `don't create thread when user is banned`() {
            val thread = thread(board, subject = "Test thread")
            every {
                threadFacade.createThread(
                    ofType(ThreadForm::class),
                    ofType(BoardDto::class)
                )
            } throws UserBannedException()

            createThread(thread.toForm()).andExpect { status { isForbidden() } }
        }

        @Test
        fun `don't create thread with invalid subject`() {
            createThread(
                thread(
                    board,
                    subject = randomString(MAX_SUBJECT_LENGTH + 1)
                ).toForm()
            ).andExpectValidationError("subject")
        }

        @Test
        fun `don't create thread without subject and body`() {
            createThread(thread(board).toForm()).andExpectValidationError("emptySubjectAndComment")
        }

        @Test
        fun `don't create thread without attachment`() {
            createThread(thread(board).toForm(), attachment = null).andExpectValidationError("uploadedAttachment")
        }
    }

    @Nested
    @DisplayName("reply to thread")
    inner class ReplyToThread {
        private fun replyToThread(
            postForm: PostForm,
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
            val post = post(thread)
            val postForm = slot<PostForm>()
            every { threadFacade.replyToThread(capture(postForm), ofType(ThreadDto::class)) } returns post.toDto()

            val response = replyToThread(post.toForm()).andExpect {
                status { isCreated() }
                jsonPath("$.thread") { doesNotExist() }
            }.andReturnConverted<PostDto>()
            response.postNumber shouldBe post.postNumber
            postForm.captured.should {
                it.ip shouldStartWith "0"
                it.attachment.originalFilename shouldBe TEST_ATTACHMENT_PNG
            }
        }

        @Test
        fun `create valid reply without attachment`() {
            val post = post(thread, body = "test")
            every { threadFacade.replyToThread(ofType(PostForm::class), ofType(ThreadDto::class)) } returns post.toDto()

            replyToThread(post.toForm()).andExpect { status { isCreated() } }.andReturnConverted<PostDto>()
                .shouldNotBeNull()
        }

        @Test
        fun `don't create reply with invalid name`() {
            replyToThread(post(thread, name = randomString(MAX_NAME_LENGTH + 1)).toForm())
                .andExpectValidationError("name")
        }

        @Test
        fun `don't create reply when user is banned`() {
            val post = post(thread, body = "test")
            every {
                threadFacade.replyToThread(
                    ofType(PostForm::class),
                    ofType(ThreadDto::class)
                )
            } throws UserBannedException()

            replyToThread(post.toForm()).andExpect { status { isForbidden() } }
        }

        @Test
        fun `don't create reply with invalid password`() {
            replyToThread(
                post(thread).toForm().apply {
                    password = randomString(
                        MAX_TRIPCODE_PASSWORD_LENGTH + 1
                    )
                }
            ).andExpectValidationError("password")
        }

        @Test
        fun `don't create reply with invalid body`() {
            replyToThread(post(thread, body = randomString(MAX_POST_LENGTH + 1)).toForm())
                .andExpectValidationError("body")
        }

        @Test
        fun `don't create reply with invalid ip`() {
            every { HttpUtils.getClientIp(any()) } returns "a.b.c.d"
            replyToThread(post(thread).toForm()).andExpectValidationError("ip")
        }

        @Test
        fun `don't create reply with attachment too big`() {
            replyToThread(
                post(thread).toForm(),
                attachment = multipartFile("attachment", MAX_ATTACHMENT_SIZE + 1)
            ).andExpectValidationError("attachmentTooBig", message = MAX_ATTACHMENT_SIZE.toString())
        }

        @Test
        fun `don't create reply without attachment and body`() {
            replyToThread(post(thread).toForm(), attachment = null)
                .andExpectValidationError("attachmentOrNonEmptyBody")
        }
    }

    @Nested
    @DisplayName("get thread")
    inner class GetThread {
        private fun getThread() = mockMvc.get(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}",
            board.label,
            thread.threadNumber
        ) { setUp() }

        @Test
        fun `get existing thread`() {
            every { threadFacade.getThread(ofType(ThreadDto::class)) } returns thread.toDto()
                .apply {
                    replies = listOf(post(thread, postNumber = 2L).toDto(), post(thread, postNumber = 3L).toDto())
                }

            val response = getThread().andExpect {
                status { isOk() }
                jsonPath("$.originalPost.thread") { doesNotExist() }
                jsonPath("$.originalPost.ip") { doesNotExist() }
                jsonPath("$.replies[*].thread") { doesNotExist() }
                jsonPath("$.replies[*].ip") { doesNotExist() }
            }.andReturnConverted<ThreadDto>()

            response.replies.shouldNotBeEmpty()
        }

        @Test
        fun `don't get non-existing thread`() {
            every { threadFacade.resolveThread(board.label, thread.threadNumber) } throws ThreadNotFoundException()

            getThread().andExpect { status { isNotFound() } }
        }
    }

    @Test
    fun `get new replies for thread`() {
        val lastPostNumber = slot<Long>()
        every {
            threadFacade.getNewReplies(
                ofType(ThreadDto::class),
                capture(lastPostNumber)
            )
        } returns listOf(post(thread).toDto(), post(thread).toDto())

        mockMvc.get(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/new-replies?lastPost=3",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect {
            status { isOk() }
            jsonPath("$[*].thread") { doesNotExist() }
            jsonPath("$[*].ip") { doesNotExist() }
        }.andReturnConverted<List<PostDto>>() shouldHaveSize 2
        lastPostNumber.captured shouldBe 3
    }

    @Test
    @WithMockJardUser(UserAuthority.TOGGLE_STICKY_THREAD)
    fun `toggle sticky on thread`() {
        every { threadFacade.toggleStickyOnThread(ofType(ThreadDto::class)) } just Runs

        mockMvc.patch(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/sticky",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }
    }

    @Test
    @WithMockJardUser(UserAuthority.TOGGLE_LOCK_THREAD)
    fun `toggle lock on thread`() {
        every { threadFacade.toggleLockOnThread(ofType(ThreadDto::class)) } just Runs

        mockMvc.patch(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/lock",
            board.label,
            thread.threadNumber
        ) { setUp() }.andExpect { status { isOk() } }
    }

    @Nested
    @DisplayName("delete post")
    @WithMockJardUser(UserAuthority.DELETE_POST)
    inner class DeletePost {
        private fun deletePost(postNumber: Long) = mockMvc.delete(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/${Mappings.PATH_VARIABLE_POST}",
            board.label,
            thread.threadNumber,
            postNumber
        ) { setUp() }

        @Test
        fun `delete post`() {
            every { threadFacade.deletePost(ofType(ThreadDto::class), ofType(PostDto::class)) } just Runs

            deletePost(post.postNumber).andExpect { status { isOk() } }
        }

        @Test
        fun `don't delete post on IO exception`() {
            every { threadFacade.deletePost(ofType(ThreadDto::class), ofType(PostDto::class)) } throws IOException()

            deletePost(post.postNumber).andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("delete own post")
    inner class DeleteOwnPost {
        private fun deleteOwnPost(postNumber: Long, deletionCode: String = "abcde") = mockMvc.delete(
            "${Mappings.API_ROOT_THREADS}/${Mappings.PATH_VARIABLE_THREAD}/${Mappings.PATH_VARIABLE_POST}/delete-own",
            board.label,
            thread.threadNumber,
            postNumber
        ) { body(DeleteOwnPostDto(deletionCode)) }

        @Test
        fun `delete own post`() {
            val deletionCode = slot<String>()
            every { postFacade.deleteOwnPost(ofType(PostDto::class), capture(deletionCode)) } just Runs

            deleteOwnPost(post.postNumber).andExpect { status { isOk() } }
            deletionCode.captured shouldBe "abcde"
        }

        @Test
        fun `don't delete own post with incorrect deletion code`() {
            every {
                postFacade.deleteOwnPost(
                    ofType(PostDto::class),
                    ofType(String::class)
                )
            } throws FormValidationException("")

            deleteOwnPost(post.postNumber).andExpect { status { isBadRequest() } }
        }

        @Test
        fun `don't delete own post on IO exception`() {
            every { postFacade.deleteOwnPost(ofType(PostDto::class), ofType(String::class)) } throws IOException()

            deleteOwnPost(post.postNumber).andExpect { status { isBadRequest() } }
        }
    }
}
