package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardFacade {

    private final BoardService boardService;

    @Autowired
    public BoardFacade(BoardService boardService) {
        this.boardService = boardService;
    }

    public List<Board> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        boards.forEach(board -> board.setThreads(null));
        return boards;
    }
}
