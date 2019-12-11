package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.controllers.validation.FormValidationException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.BoardAttachmentTypeDto;
import com.github.njuro.jboard.models.dto.forms.BoardForm;
import com.github.njuro.jboard.models.enums.BoardAttachmentType;
import com.github.njuro.jboard.services.BoardService;
import com.github.njuro.jboard.services.PostService;
import com.github.njuro.jboard.services.ThreadService;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BoardFacade {

  private final BoardService boardService;
  private final ThreadService threadService;
  private final PostService postService;

  @Autowired
  public BoardFacade(
      BoardService boardService, ThreadService threadService, PostService postService) {
    this.boardService = boardService;
    this.threadService = threadService;
    this.postService = postService;
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

  public Board editBoard(Board oldBoard, BoardForm updatedBoard) {
    oldBoard.setName(updatedBoard.getName());
    oldBoard.setAttachmentType(updatedBoard.getAttachmentType());
    oldBoard.setNsfw(updatedBoard.isNsfw());

    return boardService.updateBoard(oldBoard);
  }

  public static Set<BoardAttachmentTypeDto> getBoardTypes() {
    return Arrays.stream(BoardAttachmentType.values())
        .map(BoardAttachmentTypeDto::fromBoardAttachmentType)
        .collect(Collectors.toSet());
  }

  public Board getBoardPage(Board board, Pageable pageRequest) {
    List<Thread> threads = threadService.getThreadsFromBoard(board, pageRequest);
    threads.forEach(thread -> thread.setReplies(postService.getLatestRepliesForThread(thread)));
    board.setThreads(threads);
    return board;
  }

  public void deleteBoard(Board board) {
    threadService.deleteThreads(threadService.getAllThreadsFromBoard(board));
    boardService.deleteBoard(board);
  }
}
