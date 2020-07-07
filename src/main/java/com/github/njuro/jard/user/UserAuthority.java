package com.github.njuro.jard.user;

import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {
  MANAGE_BOARDS,
  TOGGLE_STICKY_THREAD,
  TOGGLE_LOCK_THREAD,
  DELETE_POST,
  MANAGE_USERS,
  VIEW_IP,
  MANAGE_BANS;

  @Override
  public String getAuthority() {
    return name();
  }

  public static Set<UserAuthority> getAllAuthorities() {
    return Set.of(values());
  }
}
