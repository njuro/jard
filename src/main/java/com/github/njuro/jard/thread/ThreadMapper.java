package com.github.njuro.jard.thread;

import com.github.njuro.jard.attachment.AttachmentMapper;
import com.github.njuro.jard.base.BaseMapper;
import com.github.njuro.jard.board.BoardMapper;
import com.github.njuro.jard.thread.Thread.ThreadBuilder;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.thread.dto.ThreadDto.ThreadDtoBuilder;
import com.github.njuro.jard.thread.dto.ThreadStatisticsDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = BaseMapper.Config.class,
    uses = {BoardMapper.class, AttachmentMapper.class})
public interface ThreadMapper extends BaseMapper<Thread, ThreadDto> {

  @Override
  @Mapping(target = "originalPost.thread", ignore = true)
  Thread toEntity(ThreadDto thread);

  @Override
  @Mapping(target = "originalPost.thread", ignore = true)
  ThreadDto toDto(Thread thread);

  @Override
  @Mapping(target = "originalPost.thread", ignore = true)
  Thread toExistingEntity(ThreadDto threadDto, @MappingTarget Thread threadEntity);

  @AfterMapping
  default Thread setOriginalPostThread(@MappingTarget ThreadBuilder<?, ?> threadBuilder) {
    var thread = threadBuilder.build();
    thread.getOriginalPost().setThread(thread);
    return thread;
  }

  @AfterMapping
  default ThreadDto setOriginalPostThread(@MappingTarget ThreadDtoBuilder<?, ?> threadBuilder) {
    var thread = threadBuilder.build();
    thread.getOriginalPost().setThread(thread);
    return thread;
  }

  @SuppressWarnings("unused")
  default ThreadStatistics map(ThreadStatisticsDto value) {
    return null;
  }
}
