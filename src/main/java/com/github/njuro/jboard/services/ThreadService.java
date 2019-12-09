package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.ThreadNotFoundException;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.repositories.ThreadRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service methods for manipulating {@link Thread threads}
 *
 * @author njuro
 */
@Service
@Transactional(noRollbackFor = ThreadNotFoundException.class)
public class ThreadService {

  private final ThreadRepository threadRepository;

  private final PostService postService;

  @Autowired
  public ThreadService(final ThreadRepository threadRepository, final PostService postService) {
    this.threadRepository = threadRepository;
    this.postService = postService;
  }

  public Thread saveThread(final Thread thread) {
    thread.setOriginalPost(this.postService.savePost(thread.getOriginalPost()));
    return this.threadRepository.save(thread);
  }

  public Thread resolveThread(final String boardLabel, final Long threadNumber) {
    return this.threadRepository
        .findByBoardLabelAndOriginalPostPostNumber(boardLabel, threadNumber)
        .orElseThrow(ThreadNotFoundException::new);
  }

  public Thread updateThread(final Thread thread) {
    return this.threadRepository.save(thread);
  }

  public Thread updateLastReplyTimestamp(final Thread thread) {
    thread.setLastReplyAt(LocalDateTime.now());
    return this.threadRepository.save(thread);
  }

  public void deleteThread(final Thread thread) {
    this.threadRepository.delete(thread);
    this.postService.deletePosts(thread.getPosts());
  }
}
