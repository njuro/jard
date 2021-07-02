package com.github.njuro.jard.post;

import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.AttachmentFacade;
import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.post.dto.PostForm;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.utils.validation.PropertyValidationException;
import com.github.tornaia.geoip.GeoIP;
import com.github.tornaia.geoip.GeoIPProvider;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PostFacade extends BaseFacade<Post, PostDto> {

  private final AttachmentFacade attachmentFacade;
  private final UserFacade userFacade;

  private final PostService postService;
  private final GeoIP geoIP;

  @Autowired
  public PostFacade(
      @Lazy AttachmentFacade attachmentFacade, UserFacade userFacade, PostService postService) {
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
   * @throws PropertyValidationException if post is not validated by business logic
   */
  public PostDto createPost(@Valid @NotNull PostForm postForm, ThreadDto thread) {
    PostDto post = postForm.toDto();

    if (postForm.isCapcode()) {
      UserDto current = userFacade.getCurrentUser();
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

  /** {@link PostService#savePost(Post)} */
  public PostDto savePost(PostDto post) {
    return toDto(postService.savePost(toEntity(post)));
  }

  /** {@link PostService#getAllRepliesForThread(UUID, UUID)} */
  public List<PostDto> getAllRepliesForThread(ThreadDto thread) {
    return toDtoList(
        postService.getAllRepliesForThread(thread.getId(), thread.getOriginalPost().getId()));
  }

  /** {@link PostService#getLatestRepliesForThread(UUID, UUID)} */
  public List<PostDto> getLatestRepliesForThread(ThreadDto thread) {
    return toDtoList(
        postService.getLatestRepliesForThread(thread.getId(), thread.getOriginalPost().getId()));
  }

  /** {@link PostService#getNewRepliesForThreadSince(UUID, Long)} */
  public List<PostDto> getNewRepliesForThreadSince(ThreadDto thread, Long lastPostNumber) {
    return toDtoList(postService.getNewRepliesForThreadSince(thread.getId(), lastPostNumber));
  }

  /** {@link PostService#getNumberOfPostsInThread(UUID)} )} */
  public int getNumberOfPostsInThread(ThreadDto thread) {
    return postService.getNumberOfPostsInThread(thread.getId());
  }

  /** {@link PostService#resolvePost(String, Long)} */
  public PostDto resolvePost(String boardLabel, Long postNumber) {
    return toDto(postService.resolvePost(boardLabel, postNumber));
  }

  /** {@link PostService#updatePost(Post)} */
  public PostDto updatePost(PostDto post) {
    return toDto(postService.updatePost(toEntity(post)));
  }

  /**
   * Deletes own post.
   *
   * @param post post to delete
   * @param deletionCode deletion code for given post
   * @throws PropertyValidationException if post does not have deletion code set, post its original
   *     post in its thread, or the deletion code from the request does not match
   * @throws IOException if deletion of post attachment fails
   */
  public void deleteOwnPost(PostDto post, String deletionCode) throws IOException {
    if (post.getDeletionCode() == null || post.getDeletionCode().isBlank()) {
      throw new PropertyValidationException(
          "Post does not have deletion code and cannot be deleted");
    }

    if (post.isOriginalPost()) {
      throw new PropertyValidationException("Original (first) post in thread cannot be deleted");
    }

    if (Duration.between(post.getCreatedAt(), OffsetDateTime.now()).toMinutes()
        > Constants.OWN_POST_DELETION_TIME_LIMIT) {
      throw new PropertyValidationException(
          String.format(
              "Cannot delete post older than %d minutes", Constants.OWN_POST_DELETION_TIME_LIMIT));
    }

    if (!post.getDeletionCode().equals(deletionCode)) {
      throw new PropertyValidationException("Invalid deletion code");
    }

    deletePost(post);
  }

  /** {@link PostService#deletePost(Post)} */
  public void deletePost(PostDto post) throws IOException {
    postService.deletePost(toEntity(post));
  }
}
