package com.github.njuro.jard.post;

import com.github.njuro.jard.attachment.AttachmentFacade;
import com.github.njuro.jard.thread.Thread;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

  private final AttachmentFacade attachmentFacade;
  private final PostService postService;

  @Autowired
  public PostFacade(AttachmentFacade attachmentFacade, PostService postService) {
    this.attachmentFacade = attachmentFacade;
    this.postService = postService;
  }

  public Post createPost(@Valid @NotNull PostForm postForm, Thread thread) {
    Post post = postForm.toPost();

    var boardSettings = thread.getBoard().getSettings();
    if (boardSettings.isForceDefaultPosterName()) {
      post.setName(boardSettings.getDefaultPosterName());
    }

    if (postForm.getAttachment() != null) {
      post.setAttachment(
          attachmentFacade.createAttachment(postForm.getAttachment(), thread.getBoard()));
    }

    post.setThread(thread);

    return post;
  }

  public Post resolvePost(String boardLabel, Long postNumber) {
    return postService.resolvePost(boardLabel, postNumber);
  }
}
