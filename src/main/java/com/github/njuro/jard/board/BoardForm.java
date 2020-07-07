package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jard.common.Constants.MAX_BOARD_NAME_LENGTH;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardForm {

  @NotBlank(message = "{validation.board.label.blank}")
  @Size(max = MAX_BOARD_LABEL_LENGTH, message = "{validation.board.label.length}")
  private String label;

  @NotBlank(message = "{validation.board.name.blank}")
  @Size(max = MAX_BOARD_NAME_LENGTH, message = "{validation.board.name.length}")
  private String name;

  @Valid @NotNull private BoardSettingsForm boardSettingsForm;

  public Board toBoard() {
    return Board.builder()
        .label(label)
        .name(name)
        .settings(boardSettingsForm.toBoardSettings())
        .build();
  }
}
