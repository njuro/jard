package com.github.njuro.jard.thread

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithTestDataRepository
import com.github.njuro.jard.attachment
import com.github.njuro.jard.board
import com.github.njuro.jard.post
import com.github.njuro.jard.thread
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@DataJpaTest
@WithTestDataRepository
@WithContainerDatabase
@Transactional
internal class ThreadStatisticsTest {

    @Autowired
    private lateinit var db: TestDataRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `extract thread statistics`() {
        val board = db.insert(board(label = "r"))
        val thread = db.insert(thread(board))
        db.insert(post(thread, ip = "127.0.0.1", postNumber = 2L))
        db.insert(post(thread, ip = "127.0.0.2", postNumber = 3L, attachment = attachment(filename = "1.jpg")))
        db.insert(post(thread, ip = "127.0.0.2", postNumber = 4L))
        db.insert(post(thread, ip = "127.0.0.1", postNumber = 5L, attachment = attachment(filename = "2.jpg")))
        db.insert(post(thread, ip = "127.0.0.3", postNumber = 6L, attachment = attachment(filename = "3.jpg")))
        db.insert(post(thread, ip = "127.0.0.1", postNumber = 7L, attachment = attachment(filename = "4.jpg")))
        db.insert(post(thread, ip = "127.0.0.1", postNumber = 8L, attachment = attachment(filename = "5.jpg")))
        entityManager.flush()
        entityManager.clear()

        db.select(thread).shouldBePresent().statistics.should {
            it.replyCount shouldBe 7
            it.posterCount shouldBe 4
            it.attachmentCount shouldBe 5
        }
    }
}
