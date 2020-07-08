package com.github.njuro.jard.user;

import static com.github.njuro.jard.user.UserAuthority.DELETE_POST;
import static com.github.njuro.jard.user.UserAuthority.MANAGE_BANS;
import static com.github.njuro.jard.user.UserAuthority.TOGGLE_LOCK_THREAD;
import static com.github.njuro.jard.user.UserAuthority.TOGGLE_STICKY_THREAD;
import static com.github.njuro.jard.user.UserAuthority.VIEW_IP;
import static com.github.njuro.jard.user.UserAuthority.getAllAuthorities;

import java.util.Set;
import lombok.Getter;

/**
 * Enum representing active role of a {@link User}. Role is a wrapper for group of user authorities.
 *
 * <p>TODO: allow to dynamically add/update/remove roles
 *
 * @see UserAuthority
 */
public enum UserRole {
  USER(),
  JANITOR(DELETE_POST),
  MODERATOR(DELETE_POST, TOGGLE_LOCK_THREAD, TOGGLE_STICKY_THREAD, VIEW_IP, MANAGE_BANS),
  ADMIN(getAllAuthorities());

  /** Default authorities for this role. */
  @Getter private final Set<UserAuthority> defaultAuthorites;

  UserRole(Set<UserAuthority> authorities) {
    defaultAuthorites = authorities;
  }

  UserRole(UserAuthority... authorities) {
    defaultAuthorites = Set.of(authorities);
  }
}
