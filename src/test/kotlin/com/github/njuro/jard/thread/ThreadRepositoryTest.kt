package com.github.njuro.jard.thread

import com.github.njuro.jard.UseMockDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.thread
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@DataJpaTest
@UseMockDatabase
@Transactional
internal class ThreadRepositoryTest {

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var board: Board

    @BeforeEach
    fun initializeBoard() {
        board = boardRepository.save(board(label = "r"))
    }

    @Test
    fun `find by board label and original post number`() {
        val thread = saveThread(thread(board))

        threadRepository.findByBoardLabelAndOriginalPostPostNumber(board.label, thread.originalPost.postNumber)
            .shouldBePresent {
                it.threadNumber shouldBe thread.threadNumber
            }
    }

    @Test
    fun `find by board id sorted by stickied status and last bump date`() {
        val baseTime = OffsetDateTime.now().minusWeeks(1)
        val thread1 = saveThread(thread(board, lastBumpAt = baseTime))
        val thread2 = saveThread(thread(board, lastBumpAt = baseTime.plusDays(1)))
        val thread3 = saveThread(thread(board, lastBumpAt = baseTime.minusDays(1)))
        val thread4 = saveThread(thread(board, lastBumpAt = baseTime.plusDays(2), stickied = true))
        val thread5 = saveThread(thread(board, lastBumpAt = baseTime.minusDays(2), stickied = true))

        threadRepository.findByBoardIdOrderByStickiedDescLastBumpAtDesc(board.id, Pageable.unpaged()).map {
            it.id
        }.shouldContainInOrder(thread4.id, thread5.id, thread2.id, thread1.id, thread3.id)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `find non-stickied thread with least recent last bump date`() {
        val baseTime = OffsetDateTime.now().minusWeeks(1)
        val thread1 = saveThread(thread(board, lastBumpAt = baseTime))
        val thread2 = saveThread(thread(board, lastBumpAt = baseTime.minusDays(1)))
        val thread3 = saveThread(thread(board, lastBumpAt = baseTime.minusDays(2), stickied = true))

        threadRepository.findTopByBoardIdAndStickiedFalseOrderByLastBumpAtAsc(board.id)
            .shouldBePresent { it.id shouldBe thread2.id }
    }

    @Test
    fun `count threads in board`() {
        saveThread(thread(board))
        saveThread(thread(board))
        saveThread(thread(board))

        threadRepository.countByBoardId(board.id) shouldBe 3
    }


    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }
}