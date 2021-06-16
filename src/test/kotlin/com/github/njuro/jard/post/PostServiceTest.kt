package com.github.njuro.jard.post

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.post
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@WithContainerDatabase
@Transactional
internal class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

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
        board = boardRepository.save(board(label = "r"))
        thread = threadRepository.save(thread(board).apply {
            originalPost = postRepository.save(originalPost)
        })
    }

    @Test
    fun `create new post`() {
        val post = post(thread, postNumber = 0L, body = "Test post\n")

        postService.savePost(post).should {
            it.id.shouldNotBeNull()
            it.postNumber shouldBe 1L
            it.body.shouldContain("<br/>")
        }
    }

    @Test
    fun `resolve post`() {
        val post = postRepository.save(post(thread, postNumber = 2L))

        postService.resolvePost(board.label, post.postNumber).postNumber shouldBe post.postNumber
    }

    @Test
    fun `don't resolve non-existing post`() {
        shouldThrow<PostNotFoundException> {
            postService.resolvePost(board.label, 0L)
        }
    }

    @Test
    fun `get latest replies for thread`() {
        (2L..8L).forEach { postRepository.save(post(thread, postNumber = it)) }

        postService.getLatestRepliesForThread(thread.id, thread.originalPost.id).map(Post::getPostNumber)
            .shouldContainExactly(4L, 5L, 6L, 7L, 8L)
    }

    @Test
    fun `delete single post`() {
        val reply = postRepository.save(post(thread, postNumber = 2L))

        postRepository.findById(reply.id).shouldBePresent()
        postService.deletePost(reply)
        postRepository.findById(reply.id).shouldNotBePresent()
    }

    @Test
    fun `delete multiple posts`() {
        val replies = (2L..5L).map { postRepository.save(post(thread, postNumber = it)) }

        postRepository.countByThreadId(thread.id) shouldBe 5L
        postService.deletePosts(replies)
        postRepository.countByThreadId(thread.id) shouldBe 1L
    }
}