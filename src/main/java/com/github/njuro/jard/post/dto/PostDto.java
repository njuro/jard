package com.github.njuro.jard.post.dto;

import com.github.njuro.jard.attachment.dto.AttachmentDto;
import com.github.njuro.jard.base.BaseDto;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.user.UserRole;
import java.time.OffsetDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** DTO for {@link Post}. */
@SuppressWarnings("JavadocReference")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class PostDto extends BaseDto {

  private static final long serialVersionUID = 9093403208940606160L;

  /** {@link Post#postNumber } */
  @ToString.Include @EqualsAndHashCode.Include private Long postNumber;

  /** {@link Post#name } */
  @ToString.Include private String name;

  /** {@link Post#tripcode } */
  private String tripcode;

  /** {@link Post#capcode } */
  private UserRole capcode;

  /** {@link Post#body } */
  private String body;

  /** {@link Post#createdAt } */
  private OffsetDateTime createdAt;

  /** {@link Post#ip } */
  private String ip;

  /** {@link Post#countryCode } */
  private String countryCode;

  /** {@link Post#countryName } */
  private String countryName;

  /** {@link Post#posterThreadId } */
  private String posterThreadId;

  /** {@link Post#sage } */
  private boolean sage;

  /** {@link Post#thread } */
  @EqualsAndHashCode.Include private ThreadDto thread;

  /** {@link Post#attachment } */
  private AttachmentDto attachment;
}
