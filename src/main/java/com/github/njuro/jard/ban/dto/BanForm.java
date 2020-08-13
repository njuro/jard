package com.github.njuro.jard.ban.dto;

import com.github.njuro.jard.ban.Ban;
import com.github.njuro.jard.ban.BanStatus;
import com.github.njuro.jard.common.Constants;
import java.time.OffsetDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** Form for creating/updating a {@link Ban}. */
@SuppressWarnings("JavadocReference")
@Data
@Builder
public class BanForm {

  /** {@link Ban#ip} */
  @NotNull(message = "{validation.ban.ip.null}")
  @Pattern(regexp = Constants.IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  /** {@link Ban#reason} */
  @Size(max = 1000, message = "{validation.ban.reason.max}")
  private String reason;

  /** {@link Ban#validTo} */
  @Future(message = "{validation.ban.valid.to.future}")
  private OffsetDateTime validTo;

  /** Whether this ban is only a warning. */
  private boolean warning;

  /** @return {@link BanDto} created from values of this form. */
  public BanDto toDto() {
    BanDto ban = BanDto.builder().ip(ip).reason(reason).validTo(validTo).build();
    ban.setStatus(isWarning() ? BanStatus.WARNING : BanStatus.ACTIVE);

    return ban;
  }
}
