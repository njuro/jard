package com.github.njuro.jboard.board;

import lombok.Getter;

public enum BoardAttachmentType {
  IMAGE("Image attachments allowed"),
  TEXT("No attachments allowed"),
  ALL("Image/Video/Sound/Document/Other attachments allowed");

  @Getter private String description;

  BoardAttachmentType(String description) {
    this.description = description;
  }
}
