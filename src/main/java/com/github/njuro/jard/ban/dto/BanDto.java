package com.github.njuro.jard.ban.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.njuro.jard.ban.Ban;
import com.github.njuro.jard.ban.BanStatus;
import com.github.njuro.jard.base.BaseDto;
import com.github.njuro.jard.user.dto.UserDto;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** DTO for {@link Ban}. */
@SuppressWarnings("JavadocReference")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class BanDto extends BaseDto {

  private static final long serialVersionUID = 6304040047249821945L;

  @Override
  @JsonProperty("id")
  public UUID getId() {
    return super.id;
  }

  /** {@link Ban#ip} */
  private String ip;

  /** {@link Ban#status} */
  private BanStatus status;

  /** {@link Ban#bannedBy} */
  private UserDto bannedBy;

  /** {@link Ban#reason} */
  private String reason;

  /** {@link Ban#unbannedBy} */
  private UserDto unbannedBy;

  /** {@link Ban#unbanReason} */
  private String unbanReason;

  /** {@link Ban#validFrom} */
  @ToString.Include private OffsetDateTime validFrom;

  /** {@link Ban#validTo} */
  @ToString.Include private OffsetDateTime validTo;
}
