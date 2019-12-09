package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.BoardNotFoundException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.repositories.BoardRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public BoardService(final BoardRepository boardRepository) {
    this.boardRepository = boardRepository;
  }

  public List<Board> getAllBoards() {
    return new ArrayList<>(boardRepository.findAll());
  }

  public Board saveBoard(final Board board) {
    board.setPostCounter(1L);
    return boardRepository.save(board);
  }

  public Board resolveBoard(final String label) throws BoardNotFoundException {
    return boardRepository.findByLabel(label).orElseThrow(BoardNotFoundException::new);
  }

  public boolean doesBoardExist(final String label) {
    return boardRepository.findByLabel(label).isPresent();
  }

  public Long getPostCounter(final Board board) {
    return boardRepository.getPostCounter(board.getLabel());
  }

  public void increasePostCounter(final Board board) {
    boardRepository.increasePostNumber(board.getLabel());
  }
}
