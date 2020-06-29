package com.github.njuro.jboard.ban;

import com.github.njuro.jboard.user.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ban {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String ip;

  @Enumerated(EnumType.STRING)
  private BanStatus status;

  @ManyToOne(targetEntity = User.class)
  private User bannedBy;

  private String reason;

  @ManyToOne(targetEntity = User.class)
  private User unbannedBy;

  @Size(max = 1000)
  private String unbanReason;

  private LocalDateTime start;

  private LocalDateTime end;
}
