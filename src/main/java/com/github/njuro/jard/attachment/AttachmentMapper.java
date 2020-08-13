package com.github.njuro.jard.attachment;

import com.github.njuro.jard.attachment.dto.AttachmentDto;
import com.github.njuro.jard.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.Config.class)
public interface AttachmentMapper extends BaseMapper<Attachment, AttachmentDto> {}
