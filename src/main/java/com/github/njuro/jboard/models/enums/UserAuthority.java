package com.github.njuro.jboard.models.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {
  CREATE_BOARD,
  TOGGLE_STICKY_THREAD,
  TOGGLE_LOCK_THREAD,
  DELETE_POST,
  CREATE_USER,
  EDIT_USER,
  VIEW_IP;

  @Override
  public String getAuthority() {
    return name();
  }

  public static Set<UserAuthority> getAllAuthorities() {
    return new HashSet<>(Arrays.asList(values()));
  }
}
