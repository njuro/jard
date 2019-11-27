package com.github.njuro.jboard.controllers;


import com.github.njuro.jboard.config.security.SensitiveDataFilter;
import com.github.njuro.jboard.facades.BoardFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Board;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Mappings.API_ROOT_BOARDS)
public class BoardRestController {

    private final BoardFacade boardFacade;

    @Autowired
    public BoardRestController(BoardFacade boardFacade) {
        this.boardFacade = boardFacade;
    }

    @GetMapping
    @FieldFilterSetting(className = Board.class, fields = "threads")
    public List<Board> showAllBoards() {
        return boardFacade.getAllBoards();
    }

    @GetMapping(Mappings.PATH_VARIABLE_BOARD)
    @DynamicFilter(SensitiveDataFilter.class)
    public Board showBoard(Board board) {
        return board;
    }
}
