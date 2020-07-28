package com.github.njuro.jard.post;

import static com.github.njuro.jard.common.Constants.*;

import com.github.njuro.jard.common.Constants;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** Form for creating/updating a {@link Post}. */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class PostForm {

  /** @see Post#name */
  @Size(max = MAX_NAME_LENGTH, message = "{validation.post.name.length}")
  private String name;

  /** @see Post#password */
  @Size(max = MAX_TRIPCODE_PASSWORD_LENGTH, message = "{validation.post.password.length}")
  private String password;

  /** @see Post#body */
  @Size(max = MAX_POST_LENGTH, message = "{validation.post.body.length}")
  private String body;

  /** @see Post#ip */
  @Pattern(regexp = IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  private boolean sage;

  private boolean capcode;

  /** Source file of uploaded attachment. */
  private MultipartFile attachment;

  /** URL of content to be embedded. */
  private String embedUrl;

  /**
   * Validates that attachment's size is not too big as defined by {@link
   * Constants#MAX_ATTACHMENT_SIZE}, false otherwise
   */
  @AssertFalse(message = "{validation.post.attachment.size}")
  public boolean isAttachmentTooBig() {
    return attachment != null && attachment.getSize() > MAX_ATTACHMENT_SIZE;
  }

  /** Validates that post have either non-empty body and / or attachment. */
  @AssertTrue(message = "{validation.post.empty}")
  public boolean isAttachmentOrNonEmptyBody() {
    return (embedUrl != null || (attachment != null && attachment.getSize() > 0))
        || (body != null && !body.trim().isEmpty());
  }

  /** @return {@link Post} created from values of this form. */
  public Post toPost() {
    return Post.builder()
        .name(name)
        .tripcode(HashGenerationUtils.generateTripcode(password))
        .body(body)
        .ip(ip)
        .sage(sage)
        .build();
  }
}
