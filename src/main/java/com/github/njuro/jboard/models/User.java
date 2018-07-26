package com.github.njuro.jboard.models;

import com.github.njuro.jboard.models.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

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

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Setter
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String username;

    @Basic
    @Setter
    @ToString.Exclude
    private String password;

    @Basic
    @Getter
    @Setter
    @Column(unique = true)
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
    @Column(name = "createdAt")
    @ToString.Exclude
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
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
