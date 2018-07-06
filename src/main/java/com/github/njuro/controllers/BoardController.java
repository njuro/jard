package com.github.njuro.controllers;

import com.github.njuro.exceptions.BoardNotFoundException;
import com.github.njuro.models.Board;
import com.github.njuro.services.BoardService;
import com.github.njuro.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for boards
 *
 * @author njuro
 */

@Controller
public class BoardController {

    private final BoardService boardService;

    private final ThreadService threadService;

    @Autowired
    public BoardController(BoardService boardService, ThreadService threadService) {
        this.boardService = boardService;
        this.threadService = threadService;
    }

    @GetMapping("/")
    public String showBoards(Model model) {
        model.addAttribute("title", "All boards");
        model.addAttribute("boards", boardService.getAllBoards());
        return "index";
    }

    @GetMapping("/board/{board}")
    public String showBoard(@PathVariable(name = "board") String label, Model model) {
        Board board = boardService.getBoard(label);

        if (board == null) {
            throw new BoardNotFoundException();
        }

        model.addAttribute("title", "/" + label + "/ - " + board.getName());
        model.addAttribute("board", board);
        model.addAttribute("threads", threadService.getThreadsFromBoard(board));

        return "board";
    }

}
