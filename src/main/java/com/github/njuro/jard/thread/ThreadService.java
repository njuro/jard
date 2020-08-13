package com.github.njuro.jard.thread;

import com.github.njuro.jard.post.PostService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  /**
   * Saves thread and its original post into database.
   *
   * @param thread thread to be saved
   * @return saved thread
   */
  public Thread saveThread(Thread thread) {
    thread.setOriginalPost(postService.savePost(thread.getOriginalPost()));
    return threadRepository.save(thread);
  }

  /**
   * Resolves thread from given identifiers.
   *
   * @param boardLabel label of board this thread belongs to
   * @param threadNumber number of this thread's original post
   * @return resolved thread
   * @throws ThreadNotFoundException if no such thread was found in database
   */
  public Thread resolveThread(String boardLabel, Long threadNumber) {
    return threadRepository
        .findByBoardLabelAndOriginalPostPostNumber(boardLabel, threadNumber)
        .orElseThrow(ThreadNotFoundException::new);
  }

  /**
   * Retrieves all threads from given board.
   *
   * @param boardId ID of board to get threads from
   * @return all threads from given board, ordered by stickied status and last bump timestamp
   */
  public List<Thread> getAllThreadsFromBoard(UUID boardId) {
    return getThreadsFromBoard(boardId, Pageable.unpaged());
  }

  /**
   * Retrieves (sub)collection of threads from given board.
   *
   * @param boardId ID of board to get threads from
   * @param pageRequest parameter specifying paging (requested page number and size of each page)
   * @return threads from given board, ordered by stickied status and last bump timestamp
   */
  public List<Thread> getThreadsFromBoard(UUID boardId, Pageable pageRequest) {
    return threadRepository.findByBoardIdOrderByStickiedDescLastBumpAtDesc(boardId, pageRequest);
  }

  /**
   * @param boardId ID of board to get threads from
   * @return number of active threads on given board
   */
  public int getNumberOfThreadsOnBoard(UUID boardId) {
    return threadRepository.countByBoardId(boardId).intValue();
  }

  /**
   * Saves updated thread into database.
   *
   * @param thread thread to update
   * @return updated thread
   */
  public Thread updateThread(Thread thread) {
    return threadRepository.save(thread);
  }

  /**
   * Deletes the stalest thread from given board. Stalest in this case means the thread with least
   * recent time of last bump (stickied threads are excluded).
   *
   * <p>What constitutes deleting thread is explained in * documentation of {@link
   * #deleteThread(Thread)}.
   *
   * @param boardId ID of board to delete the thread from
   * @throws IOException if deletion of one of the attachments' file fails
   */
  public void deleteStalestThread(UUID boardId) throws IOException {
    Optional<Thread> stalest =
        threadRepository.findTopByBoardIdAndStickiedFalseOrderByLastBumpAtAsc(boardId);
    if (stalest.isPresent()) {
      deleteThread(stalest.get());
    }
  }

  /**
   * Deletes given thread and all posts belonging to it along with their attachments.
   *
   * @param thread thread to delete
   * @throws IOException if deletion of one of the attachments' file fails
   */
  public void deleteThread(Thread thread) throws IOException {
    postService.deletePosts(
        postService.getAllRepliesForThread(thread.getId(), thread.getOriginalPost().getId()));
    postService.deletePost(thread.getOriginalPost());
    threadRepository.delete(thread);
  }

  /**
   * Delete all threads from given thread list. What constitutes deleting thread is explained in
   * documentation of {@link #deleteThread(Thread)}.
   *
   * @param threads list of threads to delete
   * @throws IOException if deletion of one of the attachments' file fails
   */
  public void deleteThreads(List<Thread> threads) throws IOException {
    for (Thread thread : threads) {
      deleteThread(thread);
    }
  }
}
