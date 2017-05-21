package com.github.njuro.controllers;

import com.github.njuro.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for board pages
 *
 * @author njuro
 */

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @RequestMapping("/")
    public String showBoards(Model model) {
        model.addAttribute("title", "All boards");
        model.addAttribute("boards", boardService.getAllBoards());
        return "index";
    }

    @RequestMapping("/{board}")
    public String showBoard(@PathVariable String board, Model model) {
        model.addAttribute("board", boardService.getBoard(board));
        return "board";
    }


}
