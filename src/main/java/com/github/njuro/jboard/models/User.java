package com.github.njuro.jboard.models;

import com.github.njuro.jboard.models.enums.UserAuthority;
import com.github.njuro.jboard.models.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * Entity representing registered user.
 *
 * @author njuro
 */
@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String username;

    @Basic
    @ToString.Exclude
    private String password;

    @Basic
    @Column(unique = true)
    private String email;

    @Basic
    private boolean enabled;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "authority")
    @Enumerated(value = EnumType.STRING)
    private Set<UserAuthority> authorities;

    @Basic
    @ToString.Exclude
    private String registrationIp;

    @Basic
    @ToString.Exclude
    private String lastLoginIp;

    @Column(name = "lastLogin")
    private LocalDateTime lastLogin;

    @Column(name = "createdAt")
    @ToString.Exclude
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
