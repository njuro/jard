package com.github.njuro.jard.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Entity representing registered user. */
@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User implements UserDetails {

  private static final long serialVersionUID = -6709426435122012297L;

  /** Unique identifier of this user. */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  @JsonIgnore
  private UUID id;

  /** Unique username of this user. */
  @Basic
  @Column(nullable = false, unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String username;

  /** Password of this user. */
  @Basic
  @Column(nullable = false)
  @JsonIgnore
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
  @ElementCollection(fetch = FetchType.EAGER)
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
