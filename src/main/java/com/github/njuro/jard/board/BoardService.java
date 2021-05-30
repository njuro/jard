package com.github.njuro.jard.board;

import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.thread.ThreadService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = BoardNotFoundException.class)
public class BoardService {

  private final BoardRepository boardRepository;

  private final ThreadService threadService;

  @Autowired
  public BoardService(BoardRepository boardRepository, @Lazy ThreadService threadService) {
    this.boardRepository = boardRepository;
    this.threadService = threadService;
  }

  /**
   * Saves board and its settings and sets its post counter to initial value.
   *
   * @param board board to be saved
   * @return saved board
   */
  public Board saveBoard(Board board) {
    board.setPostCounter(1L);
    board.getSettings().setBoard(board);
    return boardRepository.save(board);
  }

  /** @return all active boards sorted from least to most recent. */
  public List<Board> getAllBoards() {
    return new ArrayList<>(boardRepository.findAll(Sort.by(Board_.CREATED_AT).ascending()));
  }

  /**
   * Resolves board by given identifier.
   *
   * @param label board label
   * @return resolved board
   * @throws BoardNotFoundException if such board is not found in database
   */
  public Board resolveBoard(String label) {
    return boardRepository.findByLabel(label).orElseThrow(BoardNotFoundException::new);
  }

  /**
   * @param label board label
   * @return true if board with such label exists, false otherwise
   */
  public boolean doesBoardExist(String label) {
    return boardRepository.findByLabel(label).isPresent();
  }

  /**
   * @param board board to get post counter for
   * @return post counter for given board
   */
  public Long getPostCounter(Board board) {
    return boardRepository.getPostCounter(board.getLabel());
  }

  /**
   * Generates number for new {@link Post} on the board and increments its post counter.
   *
   * @param board board to register new board on
   * @return generated number for new post
   */
  public Long registerNewPost(Board board) {
    Long newPostNumber = getPostCounter(board);
    boardRepository.increasePostNumber(board.getLabel());
    return newPostNumber;
  }

  /**
   * Updates board and its settings.
   *
   * @param board board to update
   * @return updated board
   */
  public Board updateBoard(Board board) {
    board.getSettings().setBoardId(board.getId());
    return boardRepository.save(board);
  }

  /**
   * Deletes board. This means also deleting all of its threads. What constitutes for deleting a
   * thread is explained in documentation of {@link ThreadService#deleteThread(Thread)}.
   *
   * @param board board to delete
   * @throws IOException when deletion of one of the attachments' file fails
   */
  public void deleteBoard(Board board) throws IOException {
    threadService.deleteThreads(threadService.getAllThreadsFromBoard(board.getId()));
    boardRepository.delete(board);
  }
}
