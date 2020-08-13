package com.github.njuro.jard.user;

import com.github.njuro.jard.base.BaseMapper;
import com.github.njuro.jard.user.dto.UserDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.GrantedAuthority;

@Mapper(config = BaseMapper.Config.class)
public interface UserMapper extends BaseMapper<User, UserDto> {

  default Set<UserAuthority> map(Collection<? extends GrantedAuthority> authorities) {
    if (authorities == null) {
      return new HashSet<>();
    }

    return authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .map(UserAuthority::valueOf)
        .collect(Collectors.toSet());
  }

  @Override
  @Mapping(target = "authorities", ignore = true)
  User toExistingEntity(UserDto dto, @MappingTarget User existingEntity);

  /** Copy authorities from existing user DTO to entity. */
  @AfterMapping
  default User setAuthorities(UserDto dto, @MappingTarget User user) {
    user.setAuthorities(new HashSet<>(dto.getAuthorities()));
    return user;
  }
}
