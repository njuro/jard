package com.github.njuro.jard.post

import com.github.njuro.jard.*
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import com.github.njuro.jard.user.UserFacade
import com.github.njuro.jard.user.UserRole
import com.ninjasquad.springmockk.MockkBean
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

@UseMockDatabase
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
        thread = threadRepository.save(thread(board).apply {
            originalPost = postRepository.save(originalPost)
        })

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
    inner class DeleteOwnPost
}