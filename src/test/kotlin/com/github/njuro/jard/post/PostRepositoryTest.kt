package com.github.njuro.jard.post

import com.github.njuro.jard.UseMockDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.post
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@UseMockDatabase
@Transactional
internal class PostRepositoryTest {

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Test
    fun `find by board label and post number`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        val reply = saveReply(post(thread, postNumber = 2L))

        postRepository.findByThreadBoardLabelAndPostNumber(board.label, reply.postNumber).shouldBePresent {
            it.postNumber shouldBe reply.postNumber
        }
        postRepository.findByThreadBoardLabelAndPostNumber(board.label, 3L).shouldBeEmpty()
    }

    @Test
    fun `find by thread id and post number greater than`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        (2..4).forEach { saveReply(post(thread, postNumber = it.toLong())) }

        postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(thread.id, 2L).map(Post::getPostNumber)
            .shouldContainExactly(3L, 4L)
    }

    @Test
    fun `find by thread id excluding original post`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        (2L..4L).forEach { saveReply(post(thread, postNumber = it)) }

        postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(thread.id, thread.originalPost.id)
            .map(Post::getPostNumber)
            .shouldContainExactly(2L, 3L, 4L)
    }

    @Test
    fun `find 5 latest replies by thread id`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        (2L..8L).forEach { saveReply(post(thread, postNumber = it)) }

        postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(thread.id, thread.originalPost.id)
            .map(Post::getPostNumber)
            .shouldContainExactly(8L, 7L, 6L, 5L, 4L)
    }

    @Test
    fun `count number of posts in thread`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        (2L..5L).forEach { saveReply(post(thread, postNumber = it)) }

        postRepository.countByThreadId(thread.id) shouldBe 5
    }


    private fun saveBoard(board: Board): Board {
        return boardRepository.save(board)
    }

    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }

    private fun saveReply(reply: Post): Post {
        return postRepository.save(reply)
    }

}