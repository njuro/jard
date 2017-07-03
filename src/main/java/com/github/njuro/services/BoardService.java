package com.github.njuro.services;

import com.github.njuro.models.Board;
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

/**
 * Service for CRUD operations with boards
 *
 * @author njuro
 */

@Repository
interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByLabel(String label);

    @Query("select b.postNumber from Board b where b.label = :label")
    Long getPostNumber(@Param("label") String label);

    @Modifying
    @Query("update Board set postNumber = postNumber + 1 where label = :label")
    void increasePostNumber(@Param("label") String label);
}

@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getAllBoards() {
        List<Board> boards = new ArrayList<>();
        boards.addAll(boardRepository.findAll());
        return boards;
    }

    public Board getBoard(String label) {
        return boardRepository.findByLabel(label);
    }

    public void createBoard(Board board) {
        boardRepository.save(board);
    }

    public Long getPostNumber(String label) {
        return boardRepository.getPostNumber(label);
    }

    public void increasePostNumber(String label) {
        boardRepository.increasePostNumber(label);
    }


}