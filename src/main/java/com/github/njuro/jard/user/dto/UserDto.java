package com.github.njuro.jard.user.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.njuro.jard.base.BaseDto;
import com.github.njuro.jard.user.User;
import com.github.njuro.jard.user.UserAuthority;
import com.github.njuro.jard.user.UserRole;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** DTO for {@link User}. */
@SuppressWarnings("JavadocReference")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class UserDto extends BaseDto {
  @Serial private static final long serialVersionUID = -4790800488577238707L;

  /** {@link User#username } */
  @JsonView(PublicView.class)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String username;

  /** {@link User#email } */
  @JsonView(PublicView.class)
  @ToString.Include
  private String email;

  /** {@link User#enabled } */
  private boolean enabled;

  /** {@link User#role } */
  @JsonView(PublicView.class)
  @ToString.Include
  private UserRole role;

  /** {@link User#authorities } */
  @JsonView(PublicView.class)
  private Set<UserAuthority> authorities;

  /** {@link User#registrationIp } */
  private String registrationIp;

  /** {@link User#lastLoginIp } */
  private String lastLoginIp;

  /** {@link User#lastLogin } */
  private OffsetDateTime lastLogin;

  /** {@link User#createdAt } */
  private OffsetDateTime createdAt;

  public interface PublicView {}
}
