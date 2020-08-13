package com.github.njuro.jard.board;

import com.github.njuro.jard.base.BaseMapper;
import com.github.njuro.jard.board.dto.BoardDto;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.Config.class)
public interface BoardMapper extends BaseMapper<Board, BoardDto> {}
