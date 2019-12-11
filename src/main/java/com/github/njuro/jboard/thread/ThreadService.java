package com.github.njuro.jboard.thread;

import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.post.PostService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
  public ThreadService(ThreadRepository threadRepository, PostService postService) {
    this.threadRepository = threadRepository;
    this.postService = postService;
  }

  public Thread saveThread(Thread thread) {
    thread.setOriginalPost(postService.savePost(thread.getOriginalPost()));
    return threadRepository.save(thread);
  }

  public Thread resolveThread(String boardLabel, Long threadNumber) {
    return threadRepository
        .findByBoardLabelAndOriginalPostPostNumber(boardLabel, threadNumber)
        .orElseThrow(ThreadNotFoundException::new);
  }

  public List<Thread> getAllThreadsFromBoard(Board board) {
    return threadRepository.findByBoardId(board.getId());
  }

  public List<Thread> getThreadsFromBoard(Board board, Pageable pageRequest) {
    return threadRepository.findByBoardId(board.getId(), pageRequest);
  }

  public Thread updateThread(Thread thread) {
    return threadRepository.save(thread);
  }

  public Thread updateLastReplyTimestamp(Thread thread) {
    thread.setLastReplyAt(LocalDateTime.now());
    return threadRepository.save(thread);
  }

  public void deleteThread(Thread thread) {
    postService.deletePosts(postService.getAllRepliesForThread(thread));
    threadRepository.delete(thread);
  }

  public void deleteThreads(List<Thread> threads) {
    threads.forEach(this::deleteThread);
  }
}
