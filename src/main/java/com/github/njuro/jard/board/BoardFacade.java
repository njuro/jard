package com.github.njuro.jard.board;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.post.PostService;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.thread.ThreadService;
import com.github.njuro.jard.utils.validation.FormValidationException;
import java.io.IOException;
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

  /**
   * Creates and save new board.
   *
   * @param boardForm form with board values
   * @return created board
   * @throws FormValidationException if board with such label already exists
   */
  public Board createBoard(BoardForm boardForm) {
    if (boardService.doesBoardExist(boardForm.getLabel())) {
      throw new FormValidationException("Board with this label already exists");
    }

    return boardService.saveBoard(boardForm.toBoard());
  }

  /** @see BoardService#resolveBoard(String) */
  public Board resolveBoard(String label) {
    return boardService.resolveBoard(label);
  }

  /**
   * Retrieves board with (sub)collection of its threads. Each retrieved thread has set original
   * post and up to 5 most recent replies.
   *
   * @param board board to get
   * @param pagination parameter specifying pagination of threads
   */
  public Board getBoard(Board board, Pageable pagination) {
    List<Thread> threads = threadService.getThreadsFromBoard(board, pagination);
    threads.forEach(thread -> thread.setReplies(postService.getLatestRepliesForThread(thread)));
    board.setThreads(threads);
    return board;
  }

  public List<Board> getAllBoards() {
    return boardService.getAllBoards();
  }

  /**
   * Retrieves board's catalog.
   *
   * @param board board to get catalog for
   * @return board with all of its threads (each thread has only original post set - not replies)
   */
  public Board getBoardCatalog(Board board) {
    board.setThreads(threadService.getAllThreadsFromBoard(board));
    return board;
  }

  /**
   * @return all attachment categories mapped to their previews
   * @see AttachmentCategory
   */
  public Set<AttachmentCategory.Preview> getAttachmentCategories() {
    return Arrays.stream(AttachmentCategory.values())
        .map(AttachmentCategory::getPreview)
        .collect(Collectors.toSet());
  }

  /**
   * Checks if given MIME type is supported on given board based on allowed attachment categories on
   * this board.
   *
   * @param board Board to check MIME type on
   * @param mimeType MIME type to check - case insensitive
   * @return true if MIME type is supported on given board, false otherwise
   * @see AttachmentCategory
   */
  public boolean isMimeTypeSupported(Board board, String mimeType) {
    return board.getSettings().getAttachmentCategories().stream()
        .map(AttachmentCategory::getPreview)
        .flatMap(preview -> preview.getMimeTypes().stream())
        .anyMatch(mime -> mime.equalsIgnoreCase(mimeType));
  }

  /**
   * Edits a board. Only board's name and its settings can be edited.
   *
   * @param oldBoard board to edit
   * @param updatedBoard form with new values
   * @return edited board
   * @see BoardSettings
   */
  public Board editBoard(Board oldBoard, BoardForm updatedBoard) {
    oldBoard.setName(updatedBoard.getName());
    oldBoard.setSettings(updatedBoard.getBoardSettingsForm().toBoardSettings());

    return boardService.updateBoard(oldBoard);
  }

  /** @see BoardService#deleteBoard(Board) */
  public void deleteBoard(Board board) throws IOException {
    boardService.deleteBoard(board);
  }
}
