package com.github.njuro.jard.ban;

import com.github.njuro.jard.common.Constants;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

/** Form for invalidating ban before its natural expiration. */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class UnbanForm {

  /** @see Ban#ip */
  @NotNull(message = "{validation.ban.ip.null}")
  @Pattern(regexp = Constants.IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  /** @see Ban#unbanReason */
  private String reason;
}
