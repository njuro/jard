package com.github.njuro.jboard.ban;

import com.github.njuro.jboard.common.Constants;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UnbanForm {

  @NotNull
  @Pattern(regexp = Constants.IP_PATTERN)
  private String ip;

  private String reason;
}
