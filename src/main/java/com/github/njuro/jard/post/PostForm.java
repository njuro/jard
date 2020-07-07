package com.github.njuro.jard.post;

import static com.github.njuro.jard.common.Constants.IP_PATTERN;
import static com.github.njuro.jard.common.Constants.MAX_ATTACHMENT_SIZE;
import static com.github.njuro.jard.common.Constants.MAX_NAME_LENGTH;
import static com.github.njuro.jard.common.Constants.MAX_POST_LENGTH;
import static com.github.njuro.jard.common.Constants.MAX_TRIPCODE_PASSWORD_LENGTH;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** Data transfer object for "reply to thread" form */
@Data
@Builder
public class PostForm {

  @Size(max = MAX_NAME_LENGTH, message = "{validation.post.name.length}")
  private String name;

  @Size(max = MAX_TRIPCODE_PASSWORD_LENGTH, message = "{validation.post.password.length}")
  private String password;

  @Size(max = MAX_POST_LENGTH, message = "{validation.post.body.length}")
  private String body;

  @Pattern(regexp = IP_PATTERN, message = "{validation.ban.ip.pattern}")
  private String ip;

  private MultipartFile attachment;

  @AssertFalse(message = "{validation.post.attachment.size}")
  public boolean isAttachmentTooBig() {
    return attachment != null && attachment.getSize() > MAX_ATTACHMENT_SIZE;
  }

  @AssertTrue(message = "{validation.post.empty}")
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
