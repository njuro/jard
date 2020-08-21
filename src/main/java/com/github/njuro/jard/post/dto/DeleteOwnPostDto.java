package com.github.njuro.jard.post.dto;

import com.github.njuro.jard.post.Post;
import lombok.Data;

/** DTO for deleting own {@link Post} */
@SuppressWarnings("JavadocReference")
@Data
public class DeleteOwnPostDto {

  /** {@link Post#deletionCode */
  private String deletionCode;
}
