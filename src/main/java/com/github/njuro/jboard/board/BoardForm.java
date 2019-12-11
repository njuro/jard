package com.github.njuro.jboard.board;

import static com.github.njuro.jboard.common.Constants.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BOARD_NAME_LENGTH;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardForm {

  @NotBlank(message = "Board label is required")
  @Size(
      max = MAX_BOARD_LABEL_LENGTH,
      message = "Board label too long (allowed " + MAX_BOARD_LABEL_LENGTH + " chars)")
  private String label;

  @NotBlank(message = "Board name is required")
  @Size(
      max = MAX_BOARD_NAME_LENGTH,
      message = "Board name too long (allowed " + MAX_BOARD_NAME_LENGTH + " chars)")
  private String name;

  @NotNull(message = "Board type is required")
  private BoardAttachmentType attachmentType;

  private boolean nsfw;

  public Board toBoard() {
    return Board.builder()
        .label(label)
        .name(name)
        .attachmentType(attachmentType)
        .nsfw(nsfw)
        .build();
  }
}
