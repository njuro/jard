package com.github.njuro.jard.thread;

import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.post.PostService;
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
    return getThreadsFromBoard(board, Pageable.unpaged());
  }

  public List<Thread> getThreadsFromBoard(Board board, Pageable pageRequest) {
    return threadRepository.findByBoardIdOrderByStickiedDescLastReplyAtDesc(
        board.getId(), pageRequest);
  }

  public int getNumberOfThreadsOnBoard(Board board) {
    return threadRepository.countByBoardId(board.getId()).intValue();
  }

  public Thread updateThread(Thread thread) {
    return threadRepository.save(thread);
  }

  public Thread updateLastReplyTimestamp(Thread thread) {
    thread.setLastReplyAt(LocalDateTime.now());
    return threadRepository.save(thread);
  }

  public void deleteOldestThread(Board board) {
    threadRepository
        .findTopByBoardIdAndStickiedFalseOrderByLastReplyAtAsc(board.getId())
        .ifPresent(this::deleteThread);
  }

  public void deleteThread(Thread thread) {
    postService.deletePosts(postService.getAllRepliesForThread(thread));
    postService.deletePost(thread.getOriginalPost());
    threadRepository.delete(thread);
  }

  public void deleteThreads(List<Thread> threads) {
    threads.forEach(this::deleteThread);
  }
}
