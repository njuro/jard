package com.github.njuro.jboard.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThreadCatalogEntry {

  private String subject;
  private String originalPostBody;
  private boolean stickied;
  private boolean locked;
  private int replyCount;
  private int attachmentCount;
}
