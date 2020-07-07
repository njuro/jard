package com.github.njuro.jard.thread;

import static com.github.njuro.jard.common.Constants.MAX_SUBJECT_LENGTH;

import com.github.njuro.jard.post.PostForm;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object for "submit new thread" form
 *
 * @author njuro
 */
@Data
@Builder
public class ThreadForm {

  @Size(max = MAX_SUBJECT_LENGTH, message = "{validation.thread.subject.length}")
  private String subject;

  private boolean stickied;
  private boolean locked;

  @Valid @NotNull private PostForm postForm;

  @AssertFalse(message = "{validation.thread.first.post.empty}")
  public boolean isEmptySubjectAndComment() {
    return (subject == null || subject.trim().isEmpty())
        && (postForm.getBody() == null || postForm.getBody().trim().isEmpty());
  }

  @AssertTrue(message = "{validation.thread.first.post.attachment}")
  public boolean isUploadedAttachment() {
    return postForm.getAttachment().getSize() > 0;
  }

  public Thread toThread() {
    return Thread.builder().subject(subject).locked(locked).stickied(stickied).build();
  }
}
