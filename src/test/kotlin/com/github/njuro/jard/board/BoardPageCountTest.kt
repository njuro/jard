package com.github.njuro.jard.board

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithTestDataRepository
import com.github.njuro.jard.board
import com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE
import com.github.njuro.jard.thread
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@DataJpaTest
@WithTestDataRepository
@WithContainerDatabase
internal class BoardPageCountTest {

    @Autowired
    private lateinit var db: TestDataRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `calculate page count of board`() {
        val board = db.insert(board(label = "r"))
        entityManager.flush()
        entityManager.clear()
        db.select(board).shouldBePresent { it.pageCount shouldBe 0 }

        (1..MAX_THREADS_PER_PAGE).forEach {
            db.insert(thread(board), threadNumber = it.toLong())
        }
        entityManager.flush()
        entityManager.clear()
        db.select(board).shouldBePresent { it.pageCount shouldBe 1 }

        db.insert(thread(board), threadNumber = MAX_THREADS_PER_PAGE + 1L)
        entityManager.flush()
        entityManager.clear()
        db.select(board).shouldBePresent { it.pageCount shouldBe 2 }
    }
}
