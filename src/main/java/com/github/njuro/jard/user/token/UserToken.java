package com.github.njuro.jard.user.token;

import com.github.njuro.jard.user.User;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Entity represents validation token issued to {@link User} for purposes like resetting of
 * password, or confirmation of e-mail address.
 */
@Entity
@Table(name = "user_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class UserToken {

  /** (Unique) value of the token. Should be random long string. */
  @Id @EqualsAndHashCode.Include private String value;

  /** User the token was issued for. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @Fetch(FetchMode.JOIN)
  private User user;

  /** Type of the token. */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @ToString.Include
  private UserTokenType type;

  /** When was the token created. */
  @Column(nullable = false)
  @ToString.Include
  private OffsetDateTime issuedAt;

  /** When will the token expire. */
  @Column(nullable = false)
  @ToString.Include
  private OffsetDateTime expirationAt;
}
