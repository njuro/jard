package com.github.njuro.jboard.user;

import static com.github.njuro.jboard.user.UserAuthority.DELETE_POST;
import static com.github.njuro.jboard.user.UserAuthority.MANAGE_BANS;
import static com.github.njuro.jboard.user.UserAuthority.TOGGLE_LOCK_THREAD;
import static com.github.njuro.jboard.user.UserAuthority.TOGGLE_STICKY_THREAD;
import static com.github.njuro.jboard.user.UserAuthority.VIEW_IP;
import static com.github.njuro.jboard.user.UserAuthority.getAllAuthorities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

public enum UserRole {
  USER(),
  JANITOR(DELETE_POST),
  MODERATOR(DELETE_POST, TOGGLE_LOCK_THREAD, TOGGLE_STICKY_THREAD, VIEW_IP, MANAGE_BANS),
  ADMIN(getAllAuthorities());

  @Getter private final Set<UserAuthority> defaultAuthorites;

  UserRole(Set<UserAuthority> authorities) {
    defaultAuthorites = authorities;
  }

  UserRole(UserAuthority... authorities) {
    defaultAuthorites = new HashSet<>(Arrays.asList(authorities));
  }
}
