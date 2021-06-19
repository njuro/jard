package com.github.njuro.jard.ban;

import static com.github.njuro.jard.common.InputConstraints.MAX_BAN_REASON_LENGTH;

import com.github.njuro.jard.common.Constants;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** Form for invalidating ban before its natural expiration. */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class UnbanForm {

  /** {@link Ban#ip} */
  @NotNull(message = "{validation.ban.ip.null}")
  @Pattern(regexp = Constants.IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  /** {@link Ban#unbanReason} */
  @Size(max = MAX_BAN_REASON_LENGTH, message = "{validation.ban.reason.max}")
  private String reason;
}
