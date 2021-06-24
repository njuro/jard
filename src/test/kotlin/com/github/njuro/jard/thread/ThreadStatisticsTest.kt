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
    @Suppress("UNUSED_VARIABLE")
    fun `extract thread statistics`() {
        val board = saveBoard(board(label = "r"))
        val thread = saveThread(thread(board))
        val attachment1 = attachment(filename = "attachment1.jpg", metadata = metadata(mimeType = "image/jpg"))
        val attachment2 = attachment(filename = "attachment2.jpg", metadata = metadata(mimeType = "image/jpg"))
        val attachment3 = attachment(filename = "attachment3.jpg", metadata = metadata(mimeType = "image/jpg"))
        val attachment4 = attachment(filename = "attachment4.jpg", metadata = metadata(mimeType = "image/jpg"))
        val reply1 = saveReply(post(thread, ip = "127.0.0.1", postNumber = 2L))
        val reply2 = saveReply(post(thread, ip = "127.0.0.2", postNumber = 3L, attachment = attachment1))
        val reply3 = saveReply(post(thread, ip = "127.0.0.2", postNumber = 4L))
        val reply4 = saveReply(post(thread, ip = "127.0.0.1", postNumber = 5L, attachment = attachment2))
        val reply5 = saveReply(post(thread, ip = "127.0.0.3", postNumber = 6L, attachment = attachment3))
        val reply6 = saveReply(post(thread, ip = "127.0.0.1", postNumber = 7L, attachment = attachment4))
        entityManager.flush()
        entityManager.clear()

        threadRepository.findById(thread.id).shouldBePresent().statistics.should {
            it.replyCount shouldBe 6
            it.posterCount shouldBe 3
            it.attachmentCount shouldBe 4
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