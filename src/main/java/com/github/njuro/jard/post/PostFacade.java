package com.github.njuro.jard.post;

import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.AttachmentFacade;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.user.User;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.utils.validation.FormValidationException;
import com.github.tornaia.geoip.GeoIP;
import com.github.tornaia.geoip.GeoIPProvider;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

  private final AttachmentFacade attachmentFacade;
  private final UserFacade userFacade;
  private final PostService postService;
  private final GeoIP geoIP;

  @Autowired
  public PostFacade(
      AttachmentFacade attachmentFacade, UserFacade userFacade, PostService postService) {
    this.attachmentFacade = attachmentFacade;
    this.userFacade = userFacade;
    this.postService = postService;
    geoIP = GeoIPProvider.getGeoIP();
  }

  /**
   * Creates {@link Post} from {@link PostForm} and attaches it to {@link Thread}. This may
   * optionally include creating and storing {@link Attachment}, if post has one. Poster name may be
   * overwritten if containing {@link Board} enforces use of default poster name.
   *
   * @param postForm form with post
   * @param thread thread this post belongs to
   * @return created post
   * @throws FormValidationException if post is not validated by business logic
   */
  public Post createPost(@Valid @NotNull PostForm postForm, Thread thread) {
    Post post = postForm.toPost();

    if (postForm.isCapcode()) {
      User current = userFacade.getCurrentUser();
      if (current != null) {
        post.setCapcode(current.getRole());
      }
    }

    var boardSettings = thread.getBoard().getSettings();
    if (boardSettings.isForceDefaultPosterName()) {
      post.setName(boardSettings.getDefaultPosterName());
    }

    if (boardSettings.isCountryFlags()) {
      post.setCountryCode(
          geoIP.getTwoLetterCountryCode(post.getIp()).map(String::toLowerCase).orElse(null));
      post.setCountryName(geoIP.getCountryName(post.getIp()).orElse(null));
    }

    if (postForm.getEmbedUrl() != null && !postForm.getEmbedUrl().isBlank()) {
      post.setAttachment(
          attachmentFacade.createEmbeddedAttachment(postForm.getEmbedUrl(), thread.getBoard()));
    } else if (postForm.getAttachment() != null) {
      post.setAttachment(
          attachmentFacade.createAttachment(postForm.getAttachment(), thread.getBoard()));
    }

    post.setThread(thread);

    return post;
  }

  /** @see PostService#resolvePost(String, Long) */
  public Post resolvePost(String boardLabel, Long postNumber) {
    return postService.resolvePost(boardLabel, postNumber);
  }
}
