package com.github.njuro.jboard.ban;

import com.github.njuro.jboard.common.Constants;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class BanForm {

  @NotNull(message = "{validation.ban.ip.null}")
  @Pattern(regexp = Constants.IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  @Size(max = 1000, message = "{validation.ban.reason.max}")
  private String reason;

  @Future(message = "{validation.ban.end.future}")
  private LocalDateTime end;

  private boolean warning;

  public Ban toBan() {
    Ban ban = Ban.builder().ip(ip).reason(reason).end(end).build();
    ban.setStatus(isWarning() ? BanStatus.WARNING : BanStatus.ACTIVE);

    return ban;
  }
}
