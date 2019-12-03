package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.controllers.validation.FormValidationException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.dto.BoardAttachmentTypeDto;
import com.github.njuro.jboard.models.dto.forms.BoardForm;
import com.github.njuro.jboard.models.enums.BoardAttachmentType;
import com.github.njuro.jboard.services.BoardService;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoardFacade {

  private final BoardService boardService;

  @Autowired
  public BoardFacade(final BoardService boardService) {
    this.boardService = boardService;
  }

  public List<Board> getAllBoards() {
    return this.boardService.getAllBoards();
  }

  public Board createBoard(final BoardForm boardForm) {
    if (this.boardService.doesBoardExist(boardForm.getLabel())) {
      throw new FormValidationException("Board with this label already exists");
    }

    return this.boardService.saveBoard(boardForm.toBoard());
  }

  public static Set<BoardAttachmentTypeDto> getBoardTypes() {
    return Arrays.stream(BoardAttachmentType.values())
        .map(BoardAttachmentTypeDto::fromBoardAttachmentType)
        .collect(Collectors.toSet());
  }
}
