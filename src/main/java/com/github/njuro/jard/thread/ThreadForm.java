package com.github.njuro.jard.thread;

import static com.github.njuro.jard.common.Constants.MAX_SUBJECT_LENGTH;

import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.post.PostForm;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** Form for creating new {@link Thread} on {@link Board}. */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class ThreadForm {

  /** @see Thread#subject */
  @Size(max = MAX_SUBJECT_LENGTH, message = "{validation.thread.subject.length}")
  private String subject;

  /** @see Thread#stickied */
  private boolean stickied;

  /** @see Thread#locked */
  private boolean locked;

  /**
   * Form for original (first) post of this thread
   *
   * @see PostForm
   */
  @Valid @NotNull private PostForm postForm;

  /** Validates that thread has either subject and / or original post has non-empty body. */
  @AssertFalse(message = "{validation.thread.first.post.empty}")
  public boolean isEmptySubjectAndComment() {
    return (subject == null || subject.trim().isEmpty())
        && (postForm.getBody() == null || postForm.getBody().trim().isEmpty());
  }

  /** Validates that original post has non-empty attachment. */
  @AssertTrue(message = "{validation.thread.first.post.attachment}")
  public boolean isUploadedAttachment() {
    return (postForm.getEmbedUrl() != null && !postForm.getEmbedUrl().isBlank())
        || (postForm.getAttachment() != null && postForm.getAttachment().getSize() > 0);
  }

  /** @return {@link Thread} created from values of this form. */
  public Thread toThread() {
    return Thread.builder().subject(subject).locked(locked).stickied(stickied).build();
  }
}
