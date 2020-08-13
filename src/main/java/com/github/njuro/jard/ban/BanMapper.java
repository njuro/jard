package com.github.njuro.jard.ban;

import com.github.njuro.jard.ban.dto.BanDto;
import com.github.njuro.jard.base.BaseMapper;
import com.github.njuro.jard.user.UserMapper;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.Config.class, uses = UserMapper.class)
public interface BanMapper extends BaseMapper<Ban, BanDto> {}
