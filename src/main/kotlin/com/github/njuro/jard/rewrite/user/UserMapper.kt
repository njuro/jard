package com.github.njuro.jard.rewrite.user

import com.github.njuro.jard.rewrite.base.BaseMapper
import com.github.njuro.jard.rewrite.user.dto.UserDto
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.security.core.GrantedAuthority

@Mapper(config = BaseMapper.Config::class)
interface UserMapper : BaseMapper<User, UserDto> {
    fun map(authorities: Collection<GrantedAuthority>): Set<UserAuthority> =
        authorities.map { UserAuthority.valueOf(it.authority) }.toSet()

    @Mapping(target = "authorities", ignore = true)
    override fun toExistingEntity(dto: UserDto, @MappingTarget existingEntity: User): User

    /** Copy authorities from existing user DTO to entity.  */
    @AfterMapping
    fun setAuthorities(dto: UserDto, @MappingTarget user: User) =
        user.apply { authorities = dto.authorities.toSet() }
}
