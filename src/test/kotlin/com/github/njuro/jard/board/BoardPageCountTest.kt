package com.github.njuro.jard.board

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@DataJpaTest
@WithContainerDatabase
internal class BoardPageCountTest {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `calculate page count of board`() {
        val board = boardRepository.save(board(label = "r"))
        entityManager.flush()
        entityManager.clear()
        boardRepository.findById(board.id).shouldBePresent { it.pageCount shouldBe 0 }

        (1..MAX_THREADS_PER_PAGE).forEach {
            saveThread(thread(board).apply { originalPost.postNumber = it.toLong() })
        }
        entityManager.flush()
        entityManager.clear()
        boardRepository.findById(board.id).shouldBePresent { it.pageCount shouldBe 1 }

        saveThread(thread(board).apply { originalPost.postNumber = MAX_THREADS_PER_PAGE + 1L })
        entityManager.flush()
        entityManager.clear()
        boardRepository.findById(board.id).shouldBePresent { it.pageCount shouldBe 2 }
    }

    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }
}
