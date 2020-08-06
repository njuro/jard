package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.InputConstraints.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jard.common.InputConstraints.MAX_BOARD_NAME_LENGTH;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** Form for creating and updating {@link Board}. */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class BoardForm {

  /** @see Board#label */
  @NotBlank(message = "{validation.board.label.blank}")
  @Size(max = MAX_BOARD_LABEL_LENGTH, message = "{validation.board.label.length}")
  private String label;

  /** @see Board#name */
  @NotBlank(message = "{validation.board.name.blank}")
  @Size(max = MAX_BOARD_NAME_LENGTH, message = "{validation.board.name.length}")
  private String name;

  /** @see BoardSettingsForm */
  @Valid @NotNull private BoardSettingsForm boardSettingsForm;

  /** @return {@link Board} created from values of this form. */
  public Board toBoard() {
    return Board.builder()
        .label(label)
        .name(name)
        .settings(boardSettingsForm.toBoardSettings())
        .build();
  }
}
