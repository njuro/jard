package com.github.njuro.jard.board;

import com.github.njuro.jard.thread.ThreadService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service methods for manipulating {@link Board boards}
 *
 * @author njuro
 */
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

  public Board saveBoard(Board board) {
    board.setPostCounter(1L);
    board.getSettings().setBoard(board);
    return boardRepository.save(board);
  }

  public List<Board> getAllBoards() {
    return new ArrayList<>(boardRepository.findAll());
  }

  public Board resolveBoard(String label) throws BoardNotFoundException {
    return boardRepository.findByLabel(label).orElseThrow(BoardNotFoundException::new);
  }

  public boolean doesBoardExist(String label) {
    return boardRepository.findByLabel(label).isPresent();
  }

  public Long getPostCounter(Board board) {
    return boardRepository.getPostCounter(board.getLabel());
  }

  public Long registerNewPost(Board board) {
    Long newPostNumber = getPostCounter(board);
    boardRepository.increasePostNumber(board.getLabel());
    return newPostNumber;
  }

  public Board updateBoard(Board board) {
    board.getSettings().setBoardId(board.getId());
    return boardRepository.save(board);
  }

  public void deleteBoard(Board board) {
    threadService.deleteThreads(threadService.getAllThreadsFromBoard(board));
    boardRepository.delete(board);
  }
}
