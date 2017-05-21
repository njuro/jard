package com.github.njuro.services;

import com.github.njuro.models.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for CRUD operations with boards
 *
 * @author njuro
 */

interface BoardRepository extends CrudRepository<Board, Long> {
    Board findByLabel(String label);
}

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getAllBoards() {
        List<Board> boards = new ArrayList<>();
        boardRepository.findAll().forEach(boards::add);
        return boards;
    }

    public Board getBoard(String label) {
        return boardRepository.findByLabel(label);
    }

    public Board getBoard(Long id) {
        return boardRepository.findOne(id);
    }

    public void createBoard(Board board) {
        boardRepository.save(board);
    }

}