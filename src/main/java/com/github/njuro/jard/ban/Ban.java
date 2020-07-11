package com.github.njuro.jard.ban;

import com.github.njuro.jard.user.User;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity representing a ban on IP, which prevents it from posting. Special case of this is warning,
 * which should only warn user next time he attempts to post.
 */
@Entity
@Table(name = "bans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Ban implements Serializable {

  private static final long serialVersionUID = -3032088825735160580L;

  /** Unique identifier of this ban. */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  private UUID id;

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
  @ManyToOne(targetEntity = User.class)
  private User bannedBy;

  /** Reason for this ban (e.g. {@code spam}). */
  @Basic private String reason;

  /** (Optional) user who ended this ban before its natural expiration. */
  @ManyToOne(targetEntity = User.class)
  private User unbannedBy;

  /** (Optional) reason for ending this ban before its natural expiration */
  @Basic
  @Column(nullable = false)
  private String unbanReason;

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
