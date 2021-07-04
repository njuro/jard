package com.github.njuro.jard.thread

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithTestDataRepository
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
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
@WithTestDataRepository
@WithContainerDatabase
@Transactional
internal class ThreadRepositoryTest {

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var db: TestDataRepository

    private lateinit var board: Board

    @BeforeEach
    fun initializeBoard() {
        board = db.insert(board(label = "r"))
    }

    @Test
    fun `find by board label and original post number`() {
        val thread = db.insert(thread(board))

        threadRepository.findByBoardLabelAndOriginalPostPostNumber(board.label, thread.originalPost.postNumber)
            .shouldBePresent {
                it.threadNumber shouldBe thread.threadNumber
            }
    }

    @Test
    fun `find by board id sorted by stickied status and last bump date`() {
        val baseTime = OffsetDateTime.now().minusWeeks(1)
        val thread1 = db.insert(thread(board, lastBumpAt = baseTime), threadNumber = 1L)
        val thread2 = db.insert(thread(board, lastBumpAt = baseTime.plusDays(1)), threadNumber = 2L)
        val thread3 = db.insert(thread(board, lastBumpAt = baseTime.minusDays(1)), threadNumber = 3L)
        val thread4 = db.insert(thread(board, lastBumpAt = baseTime.plusDays(2), stickied = true), threadNumber = 4L)
        val thread5 = db.insert(thread(board, lastBumpAt = baseTime.minusDays(2), stickied = true), threadNumber = 5L)

        threadRepository.findByBoardIdOrderByStickiedDescLastBumpAtDesc(board.id, Pageable.unpaged()).map {
            it.id
        }.shouldContainInOrder(thread4.id, thread5.id, thread2.id, thread1.id, thread3.id)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `find non-stickied thread with least recent last bump date`() {
        val baseTime = OffsetDateTime.now().minusWeeks(1)
        val thread1 = db.insert(thread(board, lastBumpAt = baseTime), threadNumber = 1L)
        val thread2 = db.insert(thread(board, lastBumpAt = baseTime.minusDays(1)), threadNumber = 2L)
        val thread3 = db.insert(thread(board, lastBumpAt = baseTime.minusDays(2), stickied = true), threadNumber = 3L)

        threadRepository.findTopByBoardIdAndStickiedFalseOrderByLastBumpAtAsc(board.id)
            .shouldBePresent { it.id shouldBe thread2.id }
    }

    @Test
    fun `count threads in board`() {
        db.insert(thread(board), threadNumber = 1L)
        db.insert(thread(board), threadNumber = 2L)
        db.insert(thread(board), threadNumber = 3L)

        threadRepository.countByBoardId(board.id) shouldBe 3
    }
}
