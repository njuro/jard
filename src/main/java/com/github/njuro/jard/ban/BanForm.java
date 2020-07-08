package com.github.njuro.jard.ban;

import com.github.njuro.jard.common.Constants;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/** Form for creating/updating a {@link Ban}. */
@Data
@SuppressWarnings("JavadocReference")
public class BanForm {

  /** @see Ban#ip */
  @NotNull(message = "{validation.ban.ip.null}")
  @Pattern(regexp = Constants.IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  /** @see Ban#reason */
  @Size(max = 1000, message = "{validation.ban.reason.max}")
  private String reason;

  /** @see Ban#validTo */
  @Future(message = "{validation.ban.valid.to.future}")
  private LocalDateTime validTo;

  /** Whether this ban is only a warning. */
  private boolean warning;

  /** @return {@link Ban} created from values of this form. */
  public Ban toBan() {
    Ban ban = Ban.builder().ip(ip).reason(reason).validTo(validTo).build();
    ban.setStatus(isWarning() ? BanStatus.WARNING : BanStatus.ACTIVE);

    return ban;
  }
}
