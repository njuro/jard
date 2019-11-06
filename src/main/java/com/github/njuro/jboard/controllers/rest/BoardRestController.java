package com.github.njuro.jboard.controllers.rest;


import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.services.BoardService;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardRestController {

    private final BoardService boardService;

    private final ThreadService threadService;

    @Autowired
    public BoardRestController(BoardService boardService, ThreadService threadService) {
        this.boardService = boardService;
        this.threadService = threadService;
    }

    /**
     * Shows list of all boards
     */
    @GetMapping("/boards")
    public List<Board> showAllBoards() {
        return boardService.getAllBoards();
    }

    /**
     * Shows board specified by {board} path variable
     */
    @GetMapping("/board/{board}")
    public Board showBoard(Board board) {
        return board;
    }
}
