package com.github.njuro.jboard.models.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.njuro.jboard.models.enums.UserAuthority.*;


public enum UserRole {
    USER(),
    JANITOR(DELETE_POST),
    MODERATOR(DELETE_POST, TOGGLE_LOCK_THREAD, TOGGLE_STICKY_THREAD),
    ADMIN(getAllAuthorities());

    @Getter
    private Set<UserAuthority> defaultAuthorites;

    UserRole(Set<UserAuthority> authorities) {
        this.defaultAuthorites = authorities;
    }

    UserRole(UserAuthority... authorities) {
        this.defaultAuthorites = new HashSet<>(Arrays.asList(authorities));
    }
}
