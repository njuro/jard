package com.github.njuro.jard.user;

import static com.github.njuro.jard.user.UserAuthority.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 * Enum representing active role of a {@link User}. Role is a wrapper for group of user authorities.
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
    defaultAuthorites = new HashSet<>(authorities);
  }

  UserRole(UserAuthority... authorities) {
    defaultAuthorites = new HashSet<>(Arrays.asList(authorities));
  }
}
