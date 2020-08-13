package com.github.njuro.jard.user;

import com.github.njuro.jard.base.BaseEntity;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Set;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Entity representing registered user. */
@Entity
@Table(name = "users")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class User extends BaseEntity implements UserDetails {

  private static final long serialVersionUID = -6709426435122012297L;

  /** Unique username of this user. */
  @Basic
  @Column(nullable = false, unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String username;

  /** Password of this user. */
  @Basic
  @Column(nullable = false)
  private String password;

  /** Unique e-mail address of this user. */
  @Basic
  @Column(unique = true)
  @ToString.Include
  private String email;

  /** Whether is this user enabled */
  @Basic private boolean enabled;

  /**
   * Active role of this user.
   *
   * @see UserRole
   */
  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  @ToString.Include
  private UserRole role;

  /**
   * Authorities of this user.
   *
   * @see UserAuthority
   */
  @SuppressWarnings("JpaDataSourceORMInspection")
  @ElementCollection(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @Column(name = "authority")
  @Enumerated(value = EnumType.STRING)
  private Set<UserAuthority> authorities;

  /** IP this user registered from. */
  @Basic private String registrationIp;

  /** IP from which this user logged into system most recently. */
  @Basic private String lastLoginIp;

  /** Date and time of last login of this user. */
  private OffsetDateTime lastLogin;

  /** Date and time when this user was created. */
  @Column(nullable = false)
  private OffsetDateTime createdAt;

  /** Before inserting to database, set creation date to current date and time. */
  @PrePersist
  public void setCreatedAt() {
    createdAt = OffsetDateTime.now();
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
