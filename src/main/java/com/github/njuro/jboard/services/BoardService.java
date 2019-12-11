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
  public BoardService(BoardRepository boardRepository) {
    this.boardRepository = boardRepository;
  }

  public Board saveBoard(Board board) {
    board.setPostCounter(1L);
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

  public void increasePostCounter(Board board) {
    boardRepository.increasePostNumber(board.getLabel());
  }

  public Board updateBoard(Board board) {
    return boardRepository.save(board);
  }

  public void deleteBoard(Board board) {
    boardRepository.delete(board);
  }
}
