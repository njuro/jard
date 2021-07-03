package com.github.njuro.jard.post

import com.github.njuro.jard.MapperTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.boardSettings
import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.post
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import com.github.njuro.jard.toForm
import com.github.njuro.jard.user
import com.github.njuro.jard.user.UserFacade
import com.github.njuro.jard.user.UserRole
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@WithContainerDatabase
@Transactional
internal class PostFacadeTest : MapperTest() {

    @Autowired
    private lateinit var postFacade: PostFacade

    @MockkBean
    private lateinit var userFacade: UserFacade

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var board: Board

    private lateinit var thread: Thread

    @BeforeEach
    fun setUp() {
        board = spyk(boardRepository.save(board(label = "r")))
        thread = threadRepository.save(
            thread(board).apply {
                originalPost = postRepository.save(originalPost.apply { deletionCode = "12345" })
            }
        )

        every { userFacade.currentUser } returns user(role = UserRole.ADMIN).toDto()
    }

    @Nested
    @DisplayName("create post")
    inner class CreatePost {
        @Test
        fun `create post`() {
            val postForm = post(thread).toForm()

            postFacade.createPost(postForm, thread.toDto()).thread.id shouldBe thread.id
        }

        @Test
        fun `create post and set capcode`() {
            val postForm = post(thread).toForm().apply { isCapcode = true }

            postFacade.createPost(postForm, thread.toDto()).capcode shouldBe UserRole.ADMIN
        }

        @Test
        fun `create post and force default poster name`() {
            every { board.settings } returns boardSettings(
                forceDefaultPosterName = true,
                defaultPosterName = "Anonymous"
            )
            val postForm = post(thread).toForm().apply { name = "John" }

            postFacade.createPost(postForm, thread.toDto()).name shouldBe "Anonymous"
        }

        @Test
        fun `create post and set country data`() {
            every { board.settings } returns boardSettings(countryFlags = true)
            val postForm = post(thread).toForm().apply { ip = "185.63.157.241" }

            postFacade.createPost(postForm, thread.toDto()).should {
                it.countryCode shouldBe "sk"
                it.countryName shouldBe "Slovakia"
            }
        }
    }

    @Nested
    @DisplayName("delete own post")
    inner class DeleteOwnPost {
        @Test
        fun `delete post with valid deletion code`() {
            val reply = postRepository.save(post(thread, deletionCode = "abcde", postNumber = 2L))

            postFacade.deleteOwnPost(reply.toDto(), "abcde")
            postRepository.findById(reply.id).shouldNotBePresent()
        }

        @Test
        fun `don't delete post without deletion code`() {
            val reply = postRepository.save(post(thread, deletionCode = "", postNumber = 2L))

            shouldThrow<PropertyValidationException> {
                postFacade.deleteOwnPost(reply.toDto(), "")
            }
            postRepository.findById(reply.id).shouldBePresent()
        }

        @Test
        fun `don't delete post with invalid deletion code`() {
            val reply = postRepository.save(post(thread, deletionCode = "abcde", postNumber = 2L))

            shouldThrow<PropertyValidationException> {
                postFacade.deleteOwnPost(reply.toDto(), "xxxxx")
            }
            postRepository.findById(reply.id).shouldBePresent()
        }

        @Test
        fun `don't delete first post in thread`() {
            shouldThrow<PropertyValidationException> {
                postFacade.deleteOwnPost(thread.originalPost.toDto(), thread.originalPost.deletionCode)
            }
            postRepository.findById(thread.originalPost.id).shouldBePresent()
        }

        @Test
        fun `don't delete post after limit`() {
            val createdAt = OffsetDateTime.now().minusMinutes(Constants.OWN_POST_DELETION_TIME_LIMIT + 1L)
            val reply =
                postRepository.save(post(thread, deletionCode = "abcde", createdAt = createdAt, postNumber = 2L))

            shouldThrow<PropertyValidationException> {
                postFacade.deleteOwnPost(reply.toDto(), "abcde")
            }
            postRepository.findById(reply.id).shouldBePresent()
        }
    }
}
