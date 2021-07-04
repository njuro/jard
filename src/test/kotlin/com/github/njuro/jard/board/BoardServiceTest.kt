package com.github.njuro.jard.board

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.OffsetDateTime

@SpringBootTest
@WithContainerDatabase
internal class BoardServiceTest {

    @Autowired
    private lateinit var boardService: BoardService

    @Autowired
    private lateinit var db: TestDataRepository

    @Test
    fun `save board`() {
        val board = board("r", postCounter = 0L)

        val created = boardService.saveBoard(board)

        created.id shouldNotBe null
        created.postCounter shouldBe 1L
    }

    @Test
    fun `find all boards sorted by creation date`() {
        val baseDate = OffsetDateTime.now()
        val first = db.insert(board(label = "r", createdAt = baseDate))
        val second = db.insert(board(label = "sp", createdAt = baseDate.plusDays(1)))
        val third = db.insert(board(label = "fit", createdAt = baseDate.minusDays(1)))

        boardService.allBoards.shouldContainInOrder(third, first, second)
    }

    @Test
    fun `resolve board by label`() {
        val board = db.insert(board(label = "r"))

        boardService.resolveBoard(board.label) shouldBe board
    }

    @Test
    fun `don't resolve non-existing board`() {
        shouldThrow<BoardNotFoundException> {
            boardService.resolveBoard("xxx")
        }
    }

    @Test
    fun `register new post`() {
        val board = db.insert(board(label = "r", postCounter = 5L))

        boardService.registerNewPost(board) shouldBe board.postCounter
        db.select(board).shouldBePresent { it.postCounter shouldBe board.postCounter + 1 }
    }

    @Test
    fun `delete board`() {
        val board = db.insert(board(label = "r"))

        boardService.deleteBoard(board)
        db.select(board).shouldBeEmpty()
    }
}
