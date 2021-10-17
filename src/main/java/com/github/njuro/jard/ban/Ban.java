package com.github.njuro.jard.ban;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.user.User;
import java.io.Serial;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Entity representing a ban on IP, which prevents it from posting. Special case of this is warning,
 * which should only warn user next time he attempts to post.
 */
@Entity
@Table(name = "bans")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Ban extends BaseEntity {

  @Serial private static final long serialVersionUID = -3032088825735160580L;

  /** Banned IP (e.g. {@code 127.0.0.1}) */
  @Basic
  @Column(nullable = false)
  @ToString.Include
  private String ip;

  /**
   * Current status of this ban.
   *
   * @see BanStatus
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @ToString.Include
  private BanStatus status;

  /** User who created this ban. Can be {@code null} if the user was deleted meanwhile. */
  @ManyToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  private User bannedBy;

  /** Reason for this ban (e.g. {@code spam}). */
  @Basic private String reason;

  /** (Optional) user who ended this ban before its natural expiration. */
  @ManyToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  private User unbannedBy;

  /** (Optional) reason for ending this ban before its natural expiration */
  @Basic private String unbanReason;

  /** Date and time this ban started to be valid. */
  @Column(nullable = false)
  @ToString.Include
  private OffsetDateTime validFrom;

  /**
   * (Optional) date and time this ban expired / will expire. Value of {@code null} means the ban is
   * permanent (unless the ban is just a warning).
   */
  @ToString.Include private OffsetDateTime validTo;
}
