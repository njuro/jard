package com.github.njuro.jard

import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardMapper
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostMapper
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadMapper
import com.github.njuro.jard.thread.dto.ThreadDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal abstract class MapperTest {

    @Autowired
    protected lateinit var boardMapper: BoardMapper

    @Autowired
    protected lateinit var threadMapper: ThreadMapper

    @Autowired
    protected lateinit var postMapper: PostMapper

    fun Board.toDto(): BoardDto = boardMapper.toDto(this)

    fun Thread.toDto(): ThreadDto = threadMapper.toDto(this)

    fun Post.toDto(): PostDto = postMapper.toDto(this)

}