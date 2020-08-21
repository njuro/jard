package com.github.njuro.jard.post.dto;

import com.github.njuro.jard.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for deleting own {@link Post} */
@SuppressWarnings("JavadocReference")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteOwnPostDto {

  /** {@link Post#deletionCode */
  private String deletionCode;
}
