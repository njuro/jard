package com.github.njuro.jboard.controllers;


import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Mappings.API_ROOT_BOARDS)
public class BoardRestController {

    private final BoardService boardService;

    @Autowired
    public BoardRestController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * Shows list of all boards
     */
    @GetMapping
    public List<Board> showAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        boards.forEach(board -> board.setThreads(null));
        return boards;
    }

    @GetMapping(Mappings.PATH_VARIABLE_BOARD)
    public Board showBoard(Board board) {
        return board;
    }
}
