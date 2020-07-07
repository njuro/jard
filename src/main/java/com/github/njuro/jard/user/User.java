package com.github.njuro.jard.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
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

/**
 * Entity representing registered user.
 *
 * @author njuro
 */
@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User implements UserDetails {

  private static final long serialVersionUID = -6709426435122012297L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private UUID id;

  @Basic
  @EqualsAndHashCode.Include
  @Column(nullable = false, unique = true)
  private String username;

  @Basic @ToString.Exclude @JsonIgnore private String password;

  @Basic
  @Column(unique = true)
  private String email;

  @Basic private boolean enabled;

  @Enumerated(value = EnumType.STRING)
  private UserRole role;

  @SuppressWarnings("JpaDataSourceORMInspection")
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(name = "authority")
  @Enumerated(value = EnumType.STRING)
  private Set<UserAuthority> authorities;

  @Basic @ToString.Exclude private String registrationIp;

  @Basic @ToString.Exclude private String lastLoginIp;

  private LocalDateTime lastLogin;

  @ToString.Exclude private LocalDateTime createdAt;

  @PrePersist
  public void setCreatedAt() {
    createdAt = LocalDateTime.now();
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
