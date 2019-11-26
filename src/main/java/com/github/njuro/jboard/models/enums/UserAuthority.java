package com.github.njuro.jboard.models.enums;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum UserAuthority implements GrantedAuthority {
    TOGGLE_STICKY_THREAD, TOGGLE_LOCK_THREAD, DELETE_POST, CREATE_USER, EDIT_USER;

    public static final String DELETE_POST_VALUE = "DELETE_POST";

    @Override
    public String getAuthority() {
        return name();
    }

    public static Set<UserAuthority> getAllAuthorities() {
        return new HashSet<>(Arrays.asList(values()));
    }
}
