package com.github.njuro.jboard.post;

import static com.github.njuro.jboard.common.Constants.IP_PATTERN;
import static com.github.njuro.jboard.common.Constants.MAX_ATTACHMENT_SIZE;
import static com.github.njuro.jboard.common.Constants.MAX_NAME_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_POST_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_TRIPCODE_PASSWORD_LENGTH;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** Data transfer object for "reply to thread" form */
@Data
public class PostForm {

  @Size(
      max = MAX_NAME_LENGTH,
      message = "Username too long (allowed " + MAX_NAME_LENGTH + " chars)")
  private String name;

  @Size(
      max = MAX_TRIPCODE_PASSWORD_LENGTH,
      message = "Password too long (allowed " + MAX_TRIPCODE_PASSWORD_LENGTH + " chars)")
  private String password;

  @Size(max = MAX_POST_LENGTH, message = "Post too long (allowed " + MAX_POST_LENGTH + " chars)")
  private String body;

  @Pattern(regexp = IP_PATTERN)
  private String ip;

  private MultipartFile attachment;

  @AssertFalse(message = "Attachment is too big (allowed " + MAX_ATTACHMENT_SIZE + " bytes)")
  public boolean isAttachmentTooBig() {
    return attachment != null && attachment.getSize() > MAX_ATTACHMENT_SIZE;
  }

  @AssertTrue(message = "Post must have an attachment or non-empty body")
  public boolean isAttachmentOrNonEmptyBody() {
    return (attachment != null && attachment.getSize() > 0)
        || (body != null && !body.trim().isEmpty());
  }

  public Post toPost() {
    return Post.builder()
        .name(name)
        .tripcode(TripcodeUtils.generateTripcode(password))
        .body(body)
        .ip(ip)
        .build();
  }
}
