package com.github.njuro.jard

import com.github.njuro.jard.attachment.Attachment
import com.github.njuro.jard.attachment.AttachmentRepository
import com.github.njuro.jard.attachment.dto.AttachmentDto
import com.github.njuro.jard.ban.Ban
import com.github.njuro.jard.ban.BanRepository
import com.github.njuro.jard.ban.dto.BanDto
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import com.github.njuro.jard.thread.dto.ThreadDto
import com.github.njuro.jard.user.User
import com.github.njuro.jard.user.UserRepository
import com.github.njuro.jard.user.dto.UserDto
import com.github.njuro.jard.user.token.UserToken
import com.github.njuro.jard.user.token.UserTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("test")
internal class TestDataRepository {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userTokenRepository: UserTokenRepository

    @Autowired
    private lateinit var banRepository: BanRepository

    fun insert(board: Board) = boardRepository.save(board)

    fun insert(thread: Thread, threadNumber: Long = 1L): Thread {
        val post = insert(thread.originalPost.apply { postNumber = threadNumber })
        return threadRepository.save(thread.apply { originalPost = post })
    }

    fun insert(post: Post): Post {
        if (post.attachment != null) {
            post.attachment = insert(post.attachment)
        }
        return postRepository.save(post)
    }

    fun insert(attachment: Attachment) = attachmentRepository.save(attachment)

    fun insert(user: User) = userRepository.save(user)

    fun insert(userToken: UserToken) = userTokenRepository.save(userToken)

    fun insert(ban: Ban) = banRepository.save(ban)

    fun select(board: Board) = boardRepository.findById(board.id)

    fun select(board: BoardDto) = boardRepository.findById(board.id)

    fun select(thread: Thread) = threadRepository.findById(thread.id)

    fun select(thread: ThreadDto) = threadRepository.findById(thread.id)

    fun select(post: Post) = postRepository.findById(post.id)

    fun select(post: PostDto) = postRepository.findById(post.id)

    fun select(attachment: Attachment) = attachmentRepository.findById(attachment.id)

    fun select(attachment: AttachmentDto) = attachmentRepository.findById(attachment.id)

    fun select(user: User) = userRepository.findById(user.id)

    fun select(user: UserDto) = userRepository.findById(user.id)

    fun select(userToken: UserToken) = userTokenRepository.findById(userToken.value)

    fun select(ban: Ban) = banRepository.findById(ban.id)

    fun select(ban: BanDto) = banRepository.findById(ban.id)
}
