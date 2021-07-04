package com.github.njuro.jard.post

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithTestDataRepository
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.post
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@WithTestDataRepository
@WithContainerDatabase
@Transactional
internal class PostRepositoryTest {

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
    fun `find by board label and post number`() {
        val reply = postRepository.save(post(thread, postNumber = 2L))

        postRepository.findByThreadBoardLabelAndPostNumber(board.label, reply.postNumber).shouldBePresent {
            it.postNumber shouldBe reply.postNumber
        }
        postRepository.findByThreadBoardLabelAndPostNumber(board.label, 3L).shouldBeEmpty()
    }

    @Test
    fun `find by thread id and post number greater than`() {
        (2..4).forEach { postRepository.save(post(thread, postNumber = it.toLong())) }

        postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(thread.id, 2L).map(Post::getPostNumber)
            .shouldContainExactly(3L, 4L)
    }

    @Test
    fun `find by thread id excluding original post`() {
        (2L..4L).forEach { postRepository.save(post(thread, postNumber = it)) }

        postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(thread.id, thread.originalPost.id)
            .map(Post::getPostNumber)
            .shouldContainExactly(2L, 3L, 4L)
    }

    @Test
    fun `find 5 latest replies by thread id`() {
        (2L..8L).forEach { postRepository.save(post(thread, postNumber = it)) }

        postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(thread.id, thread.originalPost.id)
            .map(Post::getPostNumber)
            .shouldContainExactly(8L, 7L, 6L, 5L, 4L)
    }

    @Test
    fun `count number of posts in thread`() {
        (2L..5L).forEach { postRepository.save(post(thread, postNumber = it)) }

        postRepository.countByThreadId(thread.id) shouldBe 5
    }
}
