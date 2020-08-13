package com.github.njuro.jard.board.dto;

import static com.github.njuro.jard.common.InputConstraints.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jard.common.InputConstraints.MAX_BOARD_NAME_LENGTH;

import com.github.njuro.jard.board.Board;
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

  /** {@link Board#label} */
  @NotBlank(message = "{validation.board.label.blank}")
  @Size(max = MAX_BOARD_LABEL_LENGTH, message = "{validation.board.label.length}")
  private String label;

  /** {@link Board#name} */
  @NotBlank(message = "{validation.board.name.blank}")
  @Size(max = MAX_BOARD_NAME_LENGTH, message = "{validation.board.name.length}")
  private String name;

  /** {@link BoardSettingsDto } */
  @Valid @NotNull private BoardSettingsDto boardSettingsForm;

  /** @return {@link BoardDto} created from values of this form */
  public BoardDto toDto() {
    return BoardDto.builder().label(label).name(name).settings(boardSettingsForm).build();
  }
}
