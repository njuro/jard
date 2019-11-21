package com.github.njuro.jboard.models.dto;

import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.utils.Tripcodes;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import static com.github.njuro.jboard.helpers.Constants.*;

/**
 * Data transfer object for "reply to thread" form
 */
@Data
public class PostForm {

    @Size(max = MAX_NAME_LENGTH, message = "Username too long (allowed " + MAX_NAME_LENGTH + " chars)")
    private String name;

    @Size(max = MAX_TRIPCODE_PASSWORD_LENGTH, message = "Password too long (allowed " + MAX_TRIPCODE_PASSWORD_LENGTH + " chars)")
    private String password;

    @Size(max = MAX_POST_LENGTH, message = "Post too long (allowed " + MAX_POST_LENGTH + " chars)")
    private String body;

    private String ip;

    private MultipartFile attachment;

    @AssertFalse(message = "Attachment is too big (allowed " + MAX_ATTACHMENT_SIZE + " bytes)")
    public boolean isAttachmentTooBig() {
        return attachment != null && attachment.getSize() > MAX_ATTACHMENT_SIZE;
    }

    @AssertTrue(message = "Post must have an attachment or non-empty body")
    public boolean isAttachmentOrNonEmptyBody() {
        return (attachment != null && attachment.getSize() > 0) || (body != null && !body.trim().isEmpty());
    }

    public Post toPost() {
        // TODO add ip to post
        return Post.builder()
                .name(name)
                .tripcode(Tripcodes.generateTripcode(password))
                .body(body)
                .build();
    }

}
