package com.github.njuro.jboard.board;

import com.github.njuro.jboard.attachment.AttachmentType;
import com.github.njuro.jboard.post.PostService;
import com.github.njuro.jboard.thread.Thread;
import com.github.njuro.jboard.thread.ThreadService;
import com.github.njuro.jboard.utils.validation.FormValidationException;
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

  public Board createBoard(BoardForm boardForm) {
    if (boardService.doesBoardExist(boardForm.getLabel())) {
      throw new FormValidationException("Board with this label already exists");
    }

    return boardService.saveBoard(boardForm.toBoard());
  }

  public Board resolveBoard(String label) {
    return boardService.resolveBoard(label);
  }

  public Board getBoard(Board board, Pageable pagination) {
    List<Thread> threads = threadService.getThreadsFromBoard(board, pagination);
    threads.forEach(thread -> thread.setReplies(postService.getLatestRepliesForThread(thread)));
    board.setThreads(threads);
    return board;
  }

  public List<Board> getAllBoards() {
    return boardService.getAllBoards();
  }

  public Board getBoardCatalog(Board board) {
    board.setThreads(threadService.getAllThreadsFromBoard(board));
    return board;
  }

  public Set<AttachmentType.Preview> getAttachmentTypes() {
    return Arrays.stream(AttachmentType.values())
        .map(AttachmentType::getPreview)
        .collect(Collectors.toSet());
  }

  public boolean isMimeTypeSupported(Board board, String mimeType) {
    return board.getAttachmentTypes().stream()
        .map(AttachmentType::getPreview)
        .flatMap(preview -> preview.getMimeTypes().stream())
        .anyMatch(mime -> mime.equalsIgnoreCase(mimeType));
  }

  public Board editBoard(Board oldBoard, BoardForm updatedBoard) {
    oldBoard.setName(updatedBoard.getName());
    oldBoard.setAttachmentTypes(updatedBoard.getAttachmentTypes());
    oldBoard.setNsfw(updatedBoard.isNsfw());
    oldBoard.setThreadLimit(updatedBoard.getThreadLimit());
    oldBoard.setBumpLimit(updatedBoard.getBumpLimit());

    return boardService.updateBoard(oldBoard);
  }

  public void deleteBoard(Board board) {
    boardService.deleteBoard(board);
  }
}
