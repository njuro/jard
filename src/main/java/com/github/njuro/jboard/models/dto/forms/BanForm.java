package com.github.njuro.jboard.models.dto.forms;

import com.github.njuro.jboard.helpers.Constants;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class BanForm {

  @NotNull
  @Pattern(regexp = Constants.IP_PATTERN)
  private String ip;

  private Long postId;

  @Size(max = 1000)
  private String reason;

  @Future private LocalDateTime end;
}
