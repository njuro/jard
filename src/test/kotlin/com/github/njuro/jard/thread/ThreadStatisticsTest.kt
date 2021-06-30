package com.github.njuro.jard.thread

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.AttachmentRepository
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostRepository
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
@WithContainerDatabase
@Transactional
internal class ThreadStatisticsTest {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `extract thread statistics`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        saveReply(post(thread, ip = "127.0.0.1", postNumber = 2L))
        saveReply(post(thread, ip = "127.0.0.2", postNumber = 3L, attachment = attachment(filename = "1.jpg")))
        saveReply(post(thread, ip = "127.0.0.2", postNumber = 4L))
        saveReply(post(thread, ip = "127.0.0.1", postNumber = 5L, attachment = attachment(filename = "2.jpg")))
        saveReply(post(thread, ip = "127.0.0.3", postNumber = 6L, attachment = attachment(filename = "3.jpg")))
        saveReply(post(thread, ip = "127.0.0.1", postNumber = 7L, attachment = attachment(filename = "4.jpg")))
        saveReply(post(thread, ip = "127.0.0.1", postNumber = 8L, attachment = attachment(filename = "5.jpg")))
        entityManager.flush()
        entityManager.clear()

        threadRepository.findById(thread.id).shouldBePresent().statistics.should {
            it.replyCount shouldBe 7
            it.posterCount shouldBe 4
            it.attachmentCount shouldBe 5
        }
    }

    private fun saveBoard(board: Board): Board {
        return boardRepository.save(board)
    }

    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }

    private fun saveReply(post: Post): Post {
        if (post.attachment != null) {
            post.attachment = attachmentRepository.save(post.attachment)
        }

        return postRepository.save(post)
    }
}
