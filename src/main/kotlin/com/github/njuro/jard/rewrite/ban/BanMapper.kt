package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.ban.dto.BanDto
import com.github.njuro.jard.rewrite.base.BaseMapper
import com.github.njuro.jard.rewrite.user.UserMapper
import org.mapstruct.Mapper

@Mapper(config = BaseMapper.Config::class, uses = [UserMapper::class])
interface BanMapper : BaseMapper<Ban, BanDto>
