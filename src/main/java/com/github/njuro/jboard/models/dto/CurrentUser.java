package com.github.njuro.jboard.models.dto;

import com.github.njuro.jboard.models.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CurrentUser {

    private String username;
    private UserRole primaryRole;
    private Set<UserRole> roles;

    public CurrentUser(String username, UserRole primaryRole, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.primaryRole = primaryRole;
        this.roles = authorities.stream()
                .map(authority ->
                        authority instanceof UserRole ? (UserRole) authority : UserRole.getByAuthority(authority.getAuthority()))
                .collect(Collectors.toSet());
    }
}
