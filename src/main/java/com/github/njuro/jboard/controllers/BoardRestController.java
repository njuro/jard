package com.github.njuro.jboard.controllers;


import com.github.njuro.jboard.config.security.SensitiveDataFilter;
import com.github.njuro.jboard.config.security.methods.HasAuthorities;
import com.github.njuro.jboard.facades.BoardFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.dto.BoardAttachmentTypeDto;
import com.github.njuro.jboard.models.dto.forms.BoardForm;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(Mappings.API_ROOT_BOARDS)
public class BoardRestController {

    private final BoardFacade boardFacade;

    @Autowired
    public BoardRestController(BoardFacade boardFacade) {
        this.boardFacade = boardFacade;
    }

    @PostMapping
    @HasAuthorities(UserAuthority.CREATE_BOARD)
    public Board createBoard(@RequestBody @Valid BoardForm boardForm) {
        return boardFacade.createBoard(boardForm);
    }

    @GetMapping("/types")
    @HasAuthorities(UserAuthority.CREATE_BOARD)
    public Set<BoardAttachmentTypeDto> getBoardTypes() {
        return boardFacade.getBoardTypes();
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
