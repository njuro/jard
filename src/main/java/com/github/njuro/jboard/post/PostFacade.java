package com.github.njuro.jboard.post;

import com.github.njuro.jboard.attachment.AttachmentFacade;
import com.github.njuro.jboard.thread.Thread;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

  private final AttachmentFacade attachmentFacade;

  @Autowired
  public PostFacade(AttachmentFacade attachmentFacade) {
    this.attachmentFacade = attachmentFacade;
  }

  public Post createPost(@Valid @NotNull PostForm postForm, Thread thread) {
    Post post = postForm.toPost();

    if (postForm.getAttachment() != null) {
      post.setAttachment(
          attachmentFacade.createAttachment(postForm.getAttachment(), thread.getBoard()));
    }

    post.setThread(thread);

    return post;
  }
}
