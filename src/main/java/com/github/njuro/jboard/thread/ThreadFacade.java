package com.github.njuro.jboard.thread;

import com.github.njuro.jboard.ban.BanService;
import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.post.Post;
import com.github.njuro.jboard.post.PostFacade;
import com.github.njuro.jboard.post.PostForm;
import com.github.njuro.jboard.post.PostService;
import com.github.njuro.jboard.utils.validation.FormValidationException;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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

  public Thread createThread(@NotNull ThreadForm threadForm, Board board) {
    if (banService.hasActiveBan(threadForm.getPostForm().getIp())) {
      throw new FormValidationException("Your IP address is banned");
    }

    Thread thread = threadForm.toThread();
    thread.setBoard(board);
    thread.setOriginalPost(postFacade.createPost(threadForm.getPostForm(), thread));
    thread.setLastReplyAt(LocalDateTime.now());

    if (threadService.getNumberOfThreadsOnBoard(board) >= board.getThreadLimit()) {
      threadService.deleteOldestThread(board);
    }

    return threadService.saveThread(thread);
  }

  public Post replyToThread(@NotNull PostForm postForm, Thread thread) {
    if (banService.hasActiveBan(postForm.getIp())) {
      throw new FormValidationException("Your IP address is banned");
    }

    if (thread.isLocked()) {
      throw new FormValidationException("Thread is locked");
    }

    Post post = postFacade.createPost(postForm, thread);
    post = postService.savePost(post);

    if (postService.getNumberOfPostsInThread(thread) <= thread.getBoard().getBumpLimit()) {
      threadService.updateLastReplyTimestamp(thread);
    }

    return post;
  }

  public Thread getThread(Thread thread) {
    List<Post> replies = postService.getAllRepliesForThread(thread);
    thread.setReplies(replies);
    return thread;
  }

  public List<Post> getNewReplies(Thread thread, Long lastPostNumber) {
    return postService.getNewRepliesForThreadSince(thread, lastPostNumber);
  }

  public Thread toggleStickyOnThread(Thread thread) {
    thread.toggleSticky();
    return threadService.updateThread(thread);
  }

  public Thread toggleLockOnThread(Thread thread) {
    thread.toggleLock();
    return threadService.updateThread(thread);
  }

  public void deletePost(Thread thread, Post post) {
    if (thread.getOriginalPost().equals(post)) {
      // delete whole thread
      threadService.deleteThread(thread);
    } else {
      // delete post
      postService.deletePost(post);
    }
  }
}
