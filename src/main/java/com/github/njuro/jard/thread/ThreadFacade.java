package com.github.njuro.jard.thread;

import com.github.njuro.jard.ban.BanService;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.post.*;
import com.github.njuro.jard.utils.validation.FormValidationException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreadFacade {

  private final ThreadService threadService;
  private final PostService postService;
  private final BanService banService;

  private final PostFacade postFacade;

  @Autowired
  public ThreadFacade(
      ThreadService threadService,
      PostService postService,
      BanService banService,
      PostFacade postFacade) {
    this.threadService = threadService;
    this.postService = postService;
    this.banService = banService;
    this.postFacade = postFacade;
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
  public Thread createThread(@NotNull ThreadForm threadForm, Board board) {
    if (banService.hasActiveBan(threadForm.getPostForm().getIp())) {
      throw new FormValidationException("Your IP address is banned");
    }

    Thread thread = threadForm.toThread();
    thread.setBoard(board);

    Post originalPost = postFacade.createPost(threadForm.getPostForm(), thread);
    thread.setOriginalPost(originalPost);
    thread.setLastReplyAt(OffsetDateTime.now());
    thread.setLastBumpAt(OffsetDateTime.now());

    originalPost.setSage(false); // original post cannot be sage
    if (threadService.getNumberOfThreadsOnBoard(board) >= board.getSettings().getThreadLimit()) {
      try {
        threadService.deleteStalestThread(board);
      } catch (IOException ex) {
        log.error("Failed to delete stalest thread", ex);
      }
    }

    thread = threadService.saveThread(thread);
    if (board.getSettings().isPosterThreadIds()) {
      originalPost.setPosterThreadId(
          HashGenerationUtils.generatePosterThreadId(originalPost.getIp(), thread.getId()));
      postService.updatePost(originalPost);
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
  public Post replyToThread(@NotNull PostForm postForm, Thread thread) {
    if (banService.hasActiveBan(postForm.getIp())) {
      throw new FormValidationException("Your IP address is banned");
      // TODO redirect to ban status page
    }

    if (thread.isLocked()) {
      throw new FormValidationException("Thread is locked");
    }

    Post post = postFacade.createPost(postForm, thread);
    if (thread.getBoard().getSettings().isPosterThreadIds()) {
      post.setPosterThreadId(
          HashGenerationUtils.generatePosterThreadId(post.getIp(), thread.getId()));
    }
    post = postService.savePost(post);
    threadService.updateLastReplyTimestamp(thread);

    if (!post.isSage()
        && postService.getNumberOfPostsInThread(thread)
            <= thread.getBoard().getSettings().getBumpLimit()) {
      threadService.updateLastBumpTimestamp(thread);
    }

    return post;
  }

  /** @see ThreadService#resolveThread(String, Long) */
  public Thread resolveThread(String boardLabel, Long threadNumber) {
    return threadService.resolveThread(boardLabel, threadNumber);
  }

  /**
   * Retrieves and set all replies to given thread.
   *
   * @param thread to get replies to
   * @return thread with its replies set
   */
  public Thread getThread(Thread thread) {
    List<Post> replies = postService.getAllRepliesForThread(thread);
    thread.setReplies(replies);
    return thread;
  }

  /** @see PostService#getNewRepliesForThreadSince(Thread, Long) */
  public List<Post> getNewReplies(Thread thread, Long lastPostNumber) {
    return postService.getNewRepliesForThreadSince(thread, lastPostNumber);
  }

  /**
   * @param thread thread to toggle stickied status on
   * @return updated thread
   */
  public Thread toggleStickyOnThread(Thread thread) {
    thread.toggleSticky();
    return threadService.updateThread(thread);
  }

  /**
   * @param thread thread to toggle locked status on
   * @return updated thread
   */
  public Thread toggleLockOnThread(Thread thread) {
    thread.toggleLock();
    return threadService.updateThread(thread);
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
  public void deletePost(Thread thread, Post post) throws IOException {
    if (thread.getOriginalPost().equals(post)) {
      // delete whole thread
      threadService.deleteThread(thread);
    } else {
      // delete post
      postService.deletePost(post);
    }
  }
}
