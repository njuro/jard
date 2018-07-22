package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.BoardNotFoundException;
import com.github.njuro.jboard.models.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByLabel(String label);

    @Query("select b.postCounter from Board b where b.label = :label")
    Long getPostCounter(@Param("label") String label);

    @Modifying
    @Query("update Board set postCounter = postCounter + 1 where label = :label")
    void increasePostNumber(@Param("label") String label);
}

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
        List<Board> boards = new ArrayList<>(boardRepository.findAll());
        return boards;
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