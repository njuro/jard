package com.github.njuro.jboard.board;

import static com.github.njuro.jboard.common.Constants.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BOARD_NAME_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BUMP_LIMIT;
import static com.github.njuro.jboard.common.Constants.MAX_THREAD_LIMIT;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardForm {

  @NotBlank(message = "{validation.board.label.null}")
  @Size(max = MAX_BOARD_LABEL_LENGTH, message = "{validation.board.label.length}")
  private String label;

  @NotBlank(message = "Board name is required")
  @Size(
      max = MAX_BOARD_NAME_LENGTH,
      message = "Board name too long (allowed " + MAX_BOARD_NAME_LENGTH + " chars)")
  private String name;

  @NotNull(message = "Board type is required")
  private BoardAttachmentType attachmentType;

  private boolean nsfw;

  @Positive(message = "Thread limit must be a positive number")
  @Max(value = MAX_THREAD_LIMIT, message = "Thread limit cannot be higher than " + MAX_THREAD_LIMIT)
  private int threadLimit;

  @Positive(message = "Bump limit must be a positive number")
  @Max(value = MAX_BUMP_LIMIT, message = "Bump limit cannot be higher than " + MAX_BUMP_LIMIT)
  private int bumpLimit;

  public Board toBoard() {
    return Board.builder()
        .label(label)
        .name(name)
        .attachmentType(attachmentType)
        .nsfw(nsfw)
        .threadLimit(threadLimit)
        .bumpLimit(bumpLimit)
        .build();
  }
}
