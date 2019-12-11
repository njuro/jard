package com.github.njuro.jboard.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BoardAttachmentTypeDto {

  private String name;
  private String description;

  public static BoardAttachmentTypeDto fromBoardAttachmentType(BoardAttachmentType type) {
    return new BoardAttachmentTypeDto(type.name(), type.getDescription());
  }
}
