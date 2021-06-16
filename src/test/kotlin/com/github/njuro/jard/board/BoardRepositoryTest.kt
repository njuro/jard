package com.github.njuro.jard.board

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@WithContainerDatabase
internal class BoardRepositoryTest {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Test
    fun `find board by label`() {
        val expectedBoard = board(label = "fit")
        boardRepository.save(expectedBoard)

        val actualBoard = boardRepository.findByLabel(expectedBoard.label)
        actualBoard.shouldBePresent { it.label shouldBe expectedBoard.label }
    }

    @Test
    fun `don't find non-existing board by label`() {
        boardRepository.findByLabel("xxx").shouldBeEmpty()
    }

    @Test
    fun `retrieve post counter of board`() {
        val board = board(label = "r", postCounter = 10)
        boardRepository.save(board)

        boardRepository.getPostCounter(board.label) shouldBe board.postCounter
    }

    @Test
    fun `increase post counter of board`() {
        val board = board(label = "sp", postCounter = 15)
        boardRepository.save(board)

        boardRepository.increasePostNumber(board.label)
        boardRepository.findByLabel(board.label).shouldBePresent { it.postCounter shouldBe board.postCounter + 1 }
    }
}