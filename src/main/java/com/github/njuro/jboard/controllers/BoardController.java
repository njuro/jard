package com.github.njuro.jboard;

import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.services.BoardService;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for boards
 *
 * @author njuro
 **/
@Controller
public class BoardController {

    private final BoardService boardService;

    private final ThreadService threadService;

    @Autowired
    public BoardController(BoardService boardService, ThreadService threadService) {
        this.boardService = boardService;
        this.threadService = threadService;
    }

    /**
     * Shows list of all boards
     */
    @GetMapping("/")
    public String showAllBoards(Model model) {
        model.addAttribute("boards", boardService.getAllBoards());

        return "index";
    }

    /**
     * Shows board specified by {board} path variable
     */
    @GetMapping("/board/{board}")
    public String showBoard(Board board, Model model) {
        model.addAttribute("board", board);
        model.addAttribute("threads", threadService.getSortedThreadsFromBoard(board));

        return "board";
    }

}
