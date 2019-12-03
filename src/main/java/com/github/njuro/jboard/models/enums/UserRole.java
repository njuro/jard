package com.github.njuro.jboard.models.enums;

import static com.github.njuro.jboard.models.enums.UserAuthority.DELETE_POST;
import static com.github.njuro.jboard.models.enums.UserAuthority.TOGGLE_LOCK_THREAD;
import static com.github.njuro.jboard.models.enums.UserAuthority.TOGGLE_STICKY_THREAD;
import static com.github.njuro.jboard.models.enums.UserAuthority.VIEW_IP;
import static com.github.njuro.jboard.models.enums.UserAuthority.getAllAuthorities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

public enum UserRole {
  USER(),
  JANITOR(DELETE_POST),
  MODERATOR(DELETE_POST, TOGGLE_LOCK_THREAD, TOGGLE_STICKY_THREAD, VIEW_IP),
  ADMIN(getAllAuthorities());

  @Getter private Set<UserAuthority> defaultAuthorites;

  UserRole(final Set<UserAuthority> authorities) {
    this.defaultAuthorites = authorities;
  }

  UserRole(final UserAuthority... authorities) {
    this.defaultAuthorites = new HashSet<>(Arrays.asList(authorities));
  }
}
