package com.github.njuro.controllers;

import com.github.njuro.models.Board;
import com.github.njuro.services.BoardService;
import com.github.njuro.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/")
    public String showBoards(Model model) {
        model.addAttribute("title", "All boards");
        model.addAttribute("boards", boardService.getAllBoards());
        return "index";
    }

    @RequestMapping("/board/{board}")
    public String showBoard(@PathVariable(name = "board") String label, Model model) {
        Board board = boardService.getBoard(label);
        model.addAttribute("title", "/" + label + "/ - " + board.getName());
        model.addAttribute("board", board);
        model.addAttribute("threads", threadService.getThreadsFromBoard(label));
        return "board";
    }

}
