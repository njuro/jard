package com.github.njuro.jard.thread

import com.github.njuro.jard.*
import com.github.njuro.jard.ban.BanFacade
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.config.security.captcha.CaptchaProvider
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.security.captcha.MockCaptchaVerificationResult
import com.github.njuro.jard.utils.validation.FormValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@WithContainerDatabase
@Transactional
internal class ThreadFacadeTest : MapperTest() {

    @Autowired
    private lateinit var threadFacade: ThreadFacade

    @MockkBean
    private lateinit var captchaProvider: CaptchaProvider

    @MockkBean
    private lateinit var banFacade: BanFacade

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Nested
    @DisplayName("create thread")
    inner class CreateThread {

        @BeforeEach
        fun initMocks() {
            every { banFacade.hasActiveBan(any()) } returns false
        }

        @Test
        fun `create valid thread`() {
            val board = saveBoard(board(label = "r"))
            val threadForm = thread(board).apply { originalPost = post(this, sage = true) }.toForm()

            val created = threadFacade.createThread(threadForm, board.toDto())
            created.board.label shouldBe board.label
            created.originalPost.shouldNotBeNull()
            created.originalPost.isSage.shouldBeFalse()
            created.threadNumber.shouldNotBeNull()
            created.createdAt.shouldNotBeNull()
            created.lastReplyAt.shouldNotBeNull()
            created.lastBumpAt.shouldNotBeNull()
            threadRepository.findByBoardLabelAndOriginalPostPostNumber(board.label, created.threadNumber)
                .shouldBePresent()
        }

        @Test
        fun `don't create thread when ip is banned`() {
            every { banFacade.hasActiveBan(any()) } returns true
            val board = saveBoard(board(label = "r"))
            val threadForm = thread(board).toForm()

            shouldThrow<FormValidationException> { threadFacade.createThread(threadForm, board.toDto()) }
        }

        @Test
        fun `create thread with valid captcha token when captcha is enabled`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(captchaEnabled = true)))
            val token = "ABCDE"
            val threadForm = thread(board).toForm().apply { postForm = this.postForm.apply { captchaToken = token } }
            every { captchaProvider.verifyCaptchaToken(token) } returns MockCaptchaVerificationResult.VALID

            threadFacade.createThread(threadForm, board.toDto()).shouldNotBeNull()
        }

        @Test
        fun `don't create thread with invalid captcha token when captcha is enabled`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(captchaEnabled = true)))
            val threadForm = thread(board).toForm().apply { postForm = this.postForm.apply { captchaToken = "ABCDE" } }
            every { captchaProvider.verifyCaptchaToken(any()) } returns MockCaptchaVerificationResult.INVALID

            shouldThrow<FormValidationException> {
                threadFacade.createThread(threadForm, board.toDto())
            }
        }

        @Test
        fun `generate poster id when board has them enabled`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(posterThreadIds = true)))
            val threadForm = thread(board).toForm()

            threadFacade.createThread(threadForm, board.toDto()).originalPost.posterThreadId.shouldNotBeNull()
        }

        @Test
        @Suppress("UNUSED_VARIABLE")
        fun `delete stalest thread when limit is exceeded`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(threadLimit = 2)))
            val thread1 = saveThread(thread(board, lastBumpAt = OffsetDateTime.now().minusWeeks(1)))
            val thread2 = saveThread(thread(board))
            val thread3 = threadFacade.createThread(thread(board).toForm(), board.toDto())

            threadRepository.findByBoardIdOrderByStickiedDescLastBumpAtDesc(board.id, Pageable.unpaged())
                .map(Thread::getId)
                .shouldContainExactly(thread3.id, thread2.id)
        }
    }

    @Nested
    @DisplayName("reply to thread")
    inner class ReplyToThread {
        @BeforeEach
        fun initMocks() {
            every { banFacade.hasActiveBan(any()) } returns false
        }

        @Test
        fun `create valid reply`() {
            val board = saveBoard(board(label = "r"))
            val thread = saveThread(thread(board))
            val originalLastReplyAt = OffsetDateTime.from(thread.lastReplyAt)
            val originalLastBumpAt = OffsetDateTime.from(thread.lastBumpAt)
            val postForm = post(thread).toForm()

            val created = threadFacade.replyToThread(postForm, thread.toDto())
            created.ip.shouldNotBeNull()
            created.postNumber shouldBe thread.threadNumber
            getUpdatedThread(thread)
                .shouldBePresent {
                    it.lastReplyAt shouldBeAfter originalLastReplyAt
                    it.lastBumpAt shouldBeAfter originalLastBumpAt
                }
        }

        @Test
        fun `don't create reply when ip is banned`() {
            val board = saveBoard(board(label = "r"))
            val thread = saveThread(thread(board))
            val postForm = post(thread).toForm()
            every { banFacade.hasActiveBan(any()) } returns true

            shouldThrow<FormValidationException> {
                threadFacade.replyToThread(postForm, thread.toDto())
            }
        }

        @Test
        fun `don't create reply when thread is locked`() {
            val board = saveBoard(board(label = "r"))
            val thread = saveThread(thread(board, locked = true))
            val postForm = post(thread).toForm()

            shouldThrow<FormValidationException> {
                threadFacade.replyToThread(postForm, thread.toDto())
            }
        }

        @Test
        fun `create reply with valid captcha token when captcha is enabled`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(captchaEnabled = true)))
            val thread = saveThread(thread(board))
            val token = "ABCDE"
            val postForm = post(thread).toForm().apply { captchaToken = token }
            every { captchaProvider.verifyCaptchaToken(token) } returns MockCaptchaVerificationResult.VALID

            threadFacade.replyToThread(postForm, thread.toDto()).shouldNotBeNull()
        }

        @Test
        fun `don't create reply with invalid captcha token when captcha is enabled`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(captchaEnabled = true)))
            val thread = saveThread(thread(board))
            val postForm = post(thread).toForm().apply { captchaToken = "ABCDE" }
            every { captchaProvider.verifyCaptchaToken(any()) } returns MockCaptchaVerificationResult.INVALID

            shouldThrow<FormValidationException> {
                threadFacade.replyToThread(postForm, thread.toDto())
            }
        }

        @Test
        fun `generate poster id when board has them enabled`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(posterThreadIds = true)))
            val thread = saveThread(thread(board))
            val postForm = post(thread).toForm()

            threadFacade.replyToThread(postForm, thread.toDto()).posterThreadId.shouldNotBeNull()
        }

        @Test
        fun `don't update last bump time when reply is sage`() {
            val board = saveBoard(board(label = "r"))
            val thread = saveThread(thread(board))
            val originalLastBumpAt = OffsetDateTime.from(thread.lastBumpAt)
            val postForm = post(thread, sage = true).toForm()

            threadFacade.replyToThread(postForm, thread.toDto())
            getUpdatedThread(thread).shouldBePresent { it.lastBumpAt shouldBe originalLastBumpAt }
        }

        @Test
        fun `don't update last bump time when post limit is exceeded`() {
            val board = saveBoard(board(label = "r", settings = boardSettings(bumpLimit = 2)))
            val thread = saveThread(thread(board))
            saveReply(post(thread, postNumber = 2L))
            saveReply(post(thread, postNumber = 3L))

            val updatedThread = getUpdatedThread(thread).get()
            val originalLastBumpAt = OffsetDateTime.from(updatedThread.lastBumpAt)
            val postForm = post(updatedThread).toForm()

            threadFacade.replyToThread(postForm, updatedThread.toDto())
            getUpdatedThread(thread).shouldBePresent { it.lastBumpAt shouldBe originalLastBumpAt }
        }
    }

    @Test
    fun `get thread with replies`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        saveReply(post(thread, postNumber = 2L))
        saveReply(post(thread, postNumber = 3L))

        val result = threadFacade.getThread(thread.toDto())
        result.shouldNotBeNull()
        result.replies shouldHaveSize 2
    }

    @Test
    fun `toggle lock on thread`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))

        threadFacade.toggleLockOnThread(thread.toDto())
        getUpdatedThread(thread).shouldBePresent { it.isLocked.shouldBeTrue() }
        threadFacade.toggleLockOnThread(thread.apply { isLocked = true }.toDto())
        getUpdatedThread(thread).shouldBePresent { it.isLocked.shouldBeFalse() }
    }

    @Test
    fun `toggle sticky on thread`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))

        threadFacade.toggleStickyOnThread(thread.toDto())
        getUpdatedThread(thread).shouldBePresent { it.isStickied.shouldBeTrue() }
        threadFacade.toggleStickyOnThread(thread.apply { isStickied = true }.toDto())
        getUpdatedThread(thread).shouldBePresent { it.isStickied.shouldBeFalse() }
    }

    @Test
    fun `delete thread`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))

        threadFacade.deletePost(thread.toDto(), thread.originalPost.toDto())
        getUpdatedThread(thread).shouldNotBePresent()
    }

    @Test
    fun `delete reply`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        val reply = saveReply(post(thread, postNumber = 2L))

        threadFacade.deletePost(thread.toDto(), reply.toDto())
        getUpdatedThread(thread).shouldBePresent()
        threadFacade.getThread(thread.toDto()).replies.shouldBeEmpty()
    }

    private fun saveBoard(board: Board): Board {
        return boardRepository.save(board)
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
}
