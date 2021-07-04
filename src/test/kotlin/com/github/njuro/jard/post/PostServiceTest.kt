package com.github.njuro.jard.post

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.post
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
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
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var db: TestDataRepository

    private lateinit var board: Board

    private lateinit var thread: Thread

    @BeforeEach
    fun setUp() {
        board = db.insert(board(label = "r"))
        thread = db.insert(thread(board))
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
        val post = db.insert(post(thread, postNumber = 2L))

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
        (2L..8L).forEach { db.insert(post(thread, postNumber = it)) }

        postService.getLatestRepliesForThread(thread.id, thread.originalPost.id).map(Post::getPostNumber)
            .shouldContainExactly(4L, 5L, 6L, 7L, 8L)
    }

    @Test
    fun `delete single post`() {
        val reply = db.insert(post(thread, postNumber = 2L))

        db.select(reply).shouldBePresent()
        postService.deletePost(reply)
        db.select(reply).shouldNotBePresent()
    }

    @Test
    fun `delete multiple posts`() {
        val replies = (2L..5L).map { db.insert(post(thread, postNumber = it)) }

        postRepository.countByThreadId(thread.id) shouldBe 5L
        postService.deletePosts(replies)
        postRepository.countByThreadId(thread.id) shouldBe 1L
    }
}
