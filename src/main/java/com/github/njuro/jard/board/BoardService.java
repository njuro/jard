package com.github.njuro.jard.board;

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
   * Saves board and its settings to database and sets its post counter ({@link Board#postCounter})
   * to initial value (1).
   *
   * @param board board to be saved
   * @return saved board
   */
  @SuppressWarnings("JavadocReference")
  public Board saveBoard(Board board) {
    board.setPostCounter(1L);
    board.getSettings().setBoard(board);
    return boardRepository.save(board);
  }

  /**
   * Retrieves all active boards.
   *
   * @return all active boards sorted by creation date (from least to most recent).
   */
  public List<Board> getAllBoards() {
    return new ArrayList<>(boardRepository.findAll(Sort.by(Board_.CREATED_AT).ascending()));
  }

  /**
   * Resolves board with given label.
   *
   * @param label board label
   * @return resolved board
   * @throws BoardNotFoundException if such board is not found in database
   */
  public Board resolveBoard(String label) {
    return boardRepository.findByLabel(label).orElseThrow(BoardNotFoundException::new);
  }

  /**
   * Checks, if board with given label exists.
   *
   * @param label board label
   * @return true if board with such label exists, false otherwise
   */
  public boolean doesBoardExist(String label) {
    return boardRepository.findByLabel(label).isPresent();
  }

  /**
   * Retrieves post counter for given board.
   *
   * @param board board to get post counter for
   * @return post counter for given board
   * @see Board#postCounter
   */
  @SuppressWarnings("JavadocReference")
  public Long getPostCounter(Board board) {
    return boardRepository.getPostCounter(board.getLabel());
  }

  /**
   * Generates post number for new post on given board (last value of board's post counter) and
   * increases post counter of that board.
   *
   * @param board board to register new post on
   * @return generated post number for new post
   * @see Board#postCounter
   */
  @SuppressWarnings("JavadocReference")
  public Long registerNewPost(Board board) {
    Long newPostNumber = getPostCounter(board);
    boardRepository.increasePostNumber(board.getLabel());
    return newPostNumber;
  }

  /**
   * Saves updated board to database.
   *
   * @param board updated board.
   * @return saved updated board
   */
  public Board updateBoard(Board board) {
    board.getSettings().setBoardId(board.getId());
    return boardRepository.save(board);
  }

  /**
   * Deletes board from database.
   *
   * <p>This also includes deleting all threads, posts and attachments from given board.
   *
   * @param board board to delete
   * @throws IOException if deletion of one of the attachments' file fails
   */
  public void deleteBoard(Board board) throws IOException {
    threadService.deleteThreads(threadService.getAllThreadsFromBoard(board.getId()));
    boardRepository.delete(board);
  }
}
