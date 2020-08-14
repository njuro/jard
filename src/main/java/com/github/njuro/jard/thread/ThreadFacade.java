package com.github.njuro.jard.thread;

import com.github.njuro.jard.ban.BanFacade;
import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.config.security.captcha.CaptchaProvider;
import com.github.njuro.jard.config.security.captcha.CaptchaVerificationResult;
import com.github.njuro.jard.post.HashGenerationUtils;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.post.PostFacade;
import com.github.njuro.jard.post.PostService;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.post.dto.PostForm;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.thread.dto.ThreadForm;
import com.github.njuro.jard.utils.validation.FormValidationException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreadFacade extends BaseFacade<Thread, ThreadDto> {

  private final PostFacade postFacade;
  private final BanFacade banFacade;

  private final ThreadService threadService;

  private final CaptchaProvider captchaProvider;

  @Autowired
  public ThreadFacade(
      ThreadService threadService,
      PostFacade postFacade,
      BanFacade banFacade,
      CaptchaProvider captchaProvider) {
    this.threadService = threadService;
    this.postFacade = postFacade;
    this.banFacade = banFacade;
    this.captchaProvider = captchaProvider;
  }

  /**
   * Creates and save new {@link Thread} and its original post. Also deletes the stalest thread from
   * containing {@link Board} if its thread limit is surpassed.
   *
   * @param threadForm form with thread data
   * @param board board the thread belongs to
   * @return created thread
   * @throws FormValidationException if poster IP is banned
   */
  public ThreadDto createThread(@NotNull ThreadForm threadForm, BoardDto board) {
    if (banFacade.hasActiveBan(threadForm.getPostForm().getIp())) {
      throw new FormValidationException("Your IP address is banned");
    }

    ThreadDto thread = threadForm.toDto();
    thread.setBoard(board);

    if (board.getSettings().isCaptchaEnabled()) {
      verifyCaptcha(threadForm.getPostForm().getCaptchaToken());
    }

    PostDto originalPost = postFacade.createPost(threadForm.getPostForm(), thread);
    thread.setOriginalPost(originalPost);
    thread.setLastReplyAt(OffsetDateTime.now());
    thread.setLastBumpAt(OffsetDateTime.now());

    originalPost.setSage(false); // original post cannot be sage
    if (threadService.getNumberOfThreadsOnBoard(board.getId())
        >= board.getSettings().getThreadLimit()) {
      try {
        threadService.deleteStalestThread(board.getId());
      } catch (IOException ex) {
        log.error("Failed to delete stalest thread", ex);
      }
    }

    thread = toDto(threadService.saveThread(toEntity(thread)));
    originalPost = thread.getOriginalPost();
    if (board.getSettings().isPosterThreadIds()) {
      originalPost.setPosterThreadId(
          HashGenerationUtils.generatePosterThreadId(originalPost.getIp(), thread.getId()));
      postFacade.updatePost(originalPost);
    }

    return thread;
  }

  /**
   * Creates and saves reply to {@link Thread}. This also means updating last reply time of given
   * thread and (if thread's bump limit is not yet surpassed) after last bump time of it.
   *
   * @param postForm form with {@link Post} data
   * @param thread thread this reply belongs to
   * @return created reply
   * @throws FormValidationException if poster IP is banned or thread is locked
   */
  public PostDto replyToThread(@NotNull PostForm postForm, ThreadDto thread) {
    if (banFacade.hasActiveBan(postForm.getIp())) {
      throw new FormValidationException("Your IP address is banned");
      // TODO redirect to ban status page
    }

    if (thread.isLocked()) {
      throw new FormValidationException("Thread is locked");
    }

    if (thread.getBoard().getSettings().isCaptchaEnabled()) {
      verifyCaptcha(postForm.getCaptchaToken());
    }

    PostDto post = postFacade.createPost(postForm, thread);
    if (thread.getBoard().getSettings().isPosterThreadIds()) {
      post.setPosterThreadId(
          HashGenerationUtils.generatePosterThreadId(post.getIp(), thread.getId()));
    }

    updateLastReplyTimestamp(thread);
    if (!post.isSage()
        && postFacade.getNumberOfPostsInThread(thread)
            <= thread.getBoard().getSettings().getBumpLimit()) {
      updateLastBumpTimestamp(thread);
    }

    return postFacade.savePost(post);
  }

  /** {@link ThreadService#resolveThread(String, Long)} */
  public ThreadDto resolveThread(String boardLabel, Long threadNumber) {
    return toDto(threadService.resolveThread(boardLabel, threadNumber));
  }

  /**
   * Retrieves and set all replies to given thread.
   *
   * @param thread to get replies to
   * @return thread with its replies set
   */
  public ThreadDto getThread(ThreadDto thread) {
    List<PostDto> replies = postFacade.getAllRepliesForThread(thread);
    thread.setReplies(replies);
    return thread;
  }

  /** {@link ThreadService#getAllThreadsFromBoard(UUID) } */
  public List<ThreadDto> getAllThreadsFromBoard(BoardDto board) {
    return toDtoList(threadService.getAllThreadsFromBoard(board.getId()));
  }

  /** {@link ThreadService#getThreadsFromBoard(UUID, Pageable)} */
  public List<ThreadDto> getThreadsFromBoard(BoardDto board, Pageable pagination) {
    return toDtoList(threadService.getThreadsFromBoard(board.getId(), pagination));
  }

  /** {@link PostService#getNewRepliesForThreadSince(UUID, Long)} */
  public List<PostDto> getNewReplies(ThreadDto thread, Long lastPostNumber) {
    return postFacade.getNewRepliesForThreadSince(thread, lastPostNumber);
  }

  /**
   * Updates time of last reply to thread to current timestamp.
   *
   * @param thread thread to update
   */
  public void updateLastReplyTimestamp(ThreadDto thread) {
    thread.setLastReplyAt(OffsetDateTime.now());
    threadService.updateThread(toEntity(thread));
  }

  /**
   * Updates time of last bump to thread to current timestamp.
   *
   * @param thread thread to update
   */
  public void updateLastBumpTimestamp(ThreadDto thread) {
    thread.setLastBumpAt(OffsetDateTime.now());
    threadService.updateThread(toEntity(thread));
  }

  /**
   * Toggles stickied status on thread.
   *
   * @param thread thread to update
   */
  public void toggleStickyOnThread(ThreadDto thread) {
    thread.toggleSticky();
    threadService.updateThread(toEntity(thread));
  }

  /**
   * Toggles locked status on thread.
   *
   * @param thread thread to update
   */
  public void toggleLockOnThread(ThreadDto thread) {
    thread.toggleLock();
    threadService.updateThread(toEntity(thread));
  }

  /**
   * Deletes post. If the post is original post of some thread, that thread is deleted too. What
   * constitutes for deletion is explained more in documentation of {@link
   * PostService#deletePost(Post)} and {@link ThreadService#deleteThread(Thread)}.
   *
   * @param thread thread containing post to be deleted
   * @param post post to delete
   * @throws IOException if deletion of one of the attachments' file fails
   */
  public void deletePost(ThreadDto thread, PostDto post) throws IOException {
    if (thread.getOriginalPost().equals(post)) {
      // delete whole thread
      threadService.deleteThread(toEntity(thread));
    } else {
      // delete post
      postFacade.deletePost(post);
    }
  }

  /**
   * Verifies CAPTCHA response token.
   *
   * @param captchaToken CAPTCHA response token to verify
   * @throws FormValidationException if verification of token failed
   */
  private void verifyCaptcha(String captchaToken) {
    CaptchaVerificationResult result = captchaProvider.verifyCaptchaToken(captchaToken);
    if (!result.isVerified()) {
      log.warn(
          String.format(
              "Captcha verification failed: [%s]", String.join(", ", result.getErrors())));
      throw new FormValidationException("Failed to verify captcha");
    }
  }
}
