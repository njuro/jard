package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.BoardNotFoundException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.repositories.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Resolves board by label
     *
     * @param label of board
     * @return board with given label
     * @throws BoardNotFoundException if board was not found
     */
    public Board resolveBoard(String label) throws BoardNotFoundException {
        return boardRepository.findByLabel(label).orElseThrow(BoardNotFoundException::new);
    }

    /**
     * Gets all boards
     *
     * @return list of boards
     */
    public List<Board> getAllBoards() {
        return new ArrayList<>(boardRepository.findAll());
    }

    /**
     * Saves board into database
     *
     * @param board to save
     */
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    /**
     * Get current post counter for specified board
     *
     * @param board
     * @return post counter of a board
     */
    public Long getPostCounter(Board board) {
        return boardRepository.getPostCounter(board.getLabel());
    }

    /**
     * Increases board's post counter by one
     *
     * @param board
     */
    public void increasePostCounter(Board board) {
        boardRepository.increasePostNumber(board.getLabel());
    }

}