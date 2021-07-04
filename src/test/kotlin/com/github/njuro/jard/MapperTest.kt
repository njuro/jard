package com.github.njuro.jard

import com.github.njuro.jard.attachment.Attachment
import com.github.njuro.jard.attachment.AttachmentMapper
import com.github.njuro.jard.attachment.dto.AttachmentDto
import com.github.njuro.jard.ban.Ban
import com.github.njuro.jard.ban.BanMapper
import com.github.njuro.jard.ban.dto.BanDto
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardMapper
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostMapper
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadMapper
import com.github.njuro.jard.thread.dto.ThreadDto
import com.github.njuro.jard.user.User
import com.github.njuro.jard.user.UserMapper
import com.github.njuro.jard.user.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal abstract class MapperTest {

    @Autowired
    protected lateinit var boardMapper: BoardMapper

    @Autowired
    protected lateinit var threadMapper: ThreadMapper

    @Autowired
    protected lateinit var postMapper: PostMapper

    @Autowired
    protected lateinit var attachmentMapper: AttachmentMapper

    @Autowired
    protected lateinit var userMapper: UserMapper

    @Autowired
    protected lateinit var banMapper: BanMapper

    protected fun Board.toDto(): BoardDto = boardMapper.toDto(this)

    protected fun Thread.toDto(): ThreadDto = threadMapper.toDto(this)

    protected fun Post.toDto(): PostDto = postMapper.toDto(this)

    protected fun Attachment.toDto(): AttachmentDto = attachmentMapper.toDto(this)

    protected fun User.toDto(): UserDto = userMapper.toDto(this)

    protected fun Ban.toDto(): BanDto = banMapper.toDto(this)
}
