package com.github.njuro.models;

import com.github.njuro.models.enums.UserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User implements UserDetails {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Setter
    @EqualsAndHashCode.Include
    private String username;

    @Basic
    @Setter
    @ToString.Exclude
    private String password;

    @Basic
    @Getter
    @Setter
    private String email;

    @Basic
    @Setter
    private boolean enabled;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Basic
    @Getter
    @Setter
    @ToString.Exclude
    private String registrationIp;

    @Basic
    @Getter
    @Setter
    @ToString.Exclude
    private String lastLoginIp;

    @Getter
    @Setter
    @Column(name = "lastLogin")
    private LocalDateTime lastLogin;

    @Getter
    @Setter
    @Column(name = "createdAt")
    @ToString.Exclude
    private LocalDateTime createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
