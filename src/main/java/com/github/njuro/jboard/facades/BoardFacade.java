package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.controllers.validation.FormValidationException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.dto.BoardAttachmentTypeDto;
import com.github.njuro.jboard.models.dto.forms.BoardForm;
import com.github.njuro.jboard.models.enums.BoardAttachmentType;
import com.github.njuro.jboard.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BoardFacade {

    private final BoardService boardService;

    @Autowired
    public BoardFacade(BoardService boardService) {
        this.boardService = boardService;
    }

    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

    public Board createBoard(BoardForm boardForm) {
        if (boardService.doesBoardExist(boardForm.getLabel())) {
            throw new FormValidationException("Board with this label already exists");
        }

        return boardService.saveBoard(boardForm.toBoard());
    }

    public Set<BoardAttachmentTypeDto> getBoardTypes() {
        return Arrays.stream(BoardAttachmentType.values())
                .map(BoardAttachmentTypeDto::fromBoardAttachmentType).collect(Collectors.toSet());
    }
}
