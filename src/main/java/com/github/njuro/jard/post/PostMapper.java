package com.github.njuro.jard.post;

import com.github.njuro.jard.attachment.AttachmentMapper;
import com.github.njuro.jard.base.BaseMapper;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.thread.ThreadMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = BaseMapper.Config.class,
    uses = {ThreadMapper.class, AttachmentMapper.class})
public interface PostMapper extends BaseMapper<Post, PostDto> {}
