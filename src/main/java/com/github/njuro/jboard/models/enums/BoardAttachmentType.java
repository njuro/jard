package com.github.njuro.jboard.models.enums;

import lombok.Getter;

public enum BoardAttachmentType {
  IMAGE("Image attachments allowed"),
  TEXT("No attachments allowed"),
  ALL("Image/Video/Sound/Document/Other attachments allowed");

  @Getter private String description;

  BoardAttachmentType(final String description) {
    this.description = description;
  }
}
