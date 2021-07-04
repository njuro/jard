package com.github.njuro.jard.thread

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.thread
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@SpringBootTest
@WithContainerDatabase
@Transactional
internal class ThreadServiceTest {

    @Autowired
    private lateinit var threadService: ThreadService

    @Autowired
    private lateinit var db: TestDataRepository

    private lateinit var board: Board

    @BeforeEach
    fun initializeBoard() {
        board = db.insert(board(label = "r"))
    }

    @Test
    fun `create thread`() {
        val thread = threadService.saveThread(thread(board))

        db.select(thread).shouldBePresent()
    }

    @Test
    fun `resolve existing thread`() {
        val thread = threadService.saveThread(thread(board))

        threadService.resolveThread(board.label, thread.threadNumber).id shouldBe thread.id
    }

    @Test
    fun `don't resolve non-existing thread`() {
        shouldThrow<ThreadNotFoundException> {
            threadService.resolveThread(board.label, -1)
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `delete stalest thread from board`() {
        val baseTime = OffsetDateTime.now().minusWeeks(1)
        val thread1 = db.insert(thread(board, lastBumpAt = baseTime, stickied = true), threadNumber = 1L)
        val thread2 = db.insert(thread(board, lastBumpAt = baseTime.minusDays(1)), threadNumber = 2L)
        val thread3 = db.insert(thread(board, lastBumpAt = baseTime.minusDays(2)), threadNumber = 3L)

        threadService.deleteStalestThread(board.id)
        threadService.getAllThreadsFromBoard(board.id).map(Thread::getId)
            .shouldContainExactlyInAnyOrder(thread1.id, thread2.id)
    }

    @Test
    fun `delete thread by id`() {
        val thread = db.insert(thread(board))

        threadService.deleteThread(thread)
        db.select(thread).shouldBeEmpty()
    }
}
