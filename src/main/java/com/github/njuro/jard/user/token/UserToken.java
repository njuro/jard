package com.github.njuro.jard.user.token;

import com.github.njuro.jard.user.User;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "user_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class UserToken {

  @Id @EqualsAndHashCode.Include private String value;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @Fetch(FetchMode.JOIN)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @ToString.Include
  private UserTokenType type;

  @Column(nullable = false)
  @ToString.Include
  private OffsetDateTime issuedAt;

  @Column(nullable = false)
  @ToString.Include
  private OffsetDateTime expirationAt;
}
