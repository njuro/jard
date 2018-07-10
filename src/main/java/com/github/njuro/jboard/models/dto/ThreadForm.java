package com.github.njuro.jboard.models.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import static com.github.njuro.jboard.helpers.Constants.MAX_SUBJECT_LENGTH;

/**
 * Data transfer object for "submit new thread" form
 *
 * @author njuro
 */
@Data
public class ThreadForm {

    @Size(max = MAX_SUBJECT_LENGTH, message = "Subject too long (allowed " + MAX_SUBJECT_LENGTH + " chars)")
    private String subject;
    private boolean stickied;
    private boolean locked;

    @Valid
    private PostForm post;

    @AssertFalse(message = "First post must have non-empty subject or non-empty body")
    public boolean isEmptySubjectAndComment() {
        return (subject == null || subject.trim().isEmpty()) &&
                (post.getBody() == null || post.getBody().trim().isEmpty());
    }

    @AssertTrue(message = "First post must have an attachment")
    public boolean isUploadedAttachment() {
        return post.getAttachment().getSize() > 0;
    }
}
