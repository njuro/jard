package com.github.njuro.jard.board;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.board.dto.BoardForm;
import com.github.njuro.jard.post.PostFacade;
import com.github.njuro.jard.thread.ThreadFacade;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.utils.validation.PropertyValidationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BoardFacade extends BaseFacade<Board, BoardDto> {

  private final ThreadFacade threadFacade;
  private final PostFacade postFacade;

  private final BoardService boardService;

  @Autowired
  public BoardFacade(ThreadFacade threadFacade, PostFacade postFacade, BoardService boardService) {
    this.threadFacade = threadFacade;
    this.postFacade = postFacade;
    this.boardService = boardService;
  }

  /**
   * Creates and saves new board.
   *
   * @param boardForm form with board data
   * @return created board
   * @throws PropertyValidationException if board with such label already exists
   */
  public BoardDto createBoard(BoardForm boardForm) {
    if (boardService.doesBoardExist(boardForm.getLabel())) {
      throw new PropertyValidationException("Board with this label already exists");
    }

    return toDto(boardService.saveBoard(toEntity(boardForm.toDto())));
  }

  /** {@link BoardService#resolveBoard(String)} */
  public BoardDto resolveBoard(String label) {
    return toDto(boardService.resolveBoard(label));
  }

  /**
   * Retrieves board with (sub)collection of its threads (depending on pagination parameters).
   *
   * <p>Each retrieved thread has set original post and up to 5 most recent replies.
   *
   * @param board board to retrieve
   * @param pagination pagination parameters for threads
   */
  public BoardDto getBoard(BoardDto board, Pageable pagination) {
    List<ThreadDto> threads = threadFacade.getThreadsFromBoard(board, pagination);
    threads.forEach(thread -> thread.setReplies(postFacade.getLatestRepliesForThread(thread)));
    board.setThreads(threads);

    return board;
  }

  /** {@link BoardService#getAllBoards()} */
  public List<BoardDto> getAllBoards() {
    return mapper.toDtoList(boardService.getAllBoards());
  }

  /**
   * Retrieves board's catalog.
   *
   * <p>Catalog is like a overview of board - it includes all threads on given board, but only with
   * their first (original) posts. Replies are not retrieved in this case.
   *
   * @param board board to get catalog for
   * @return board with all threads and their original posts.
   */
  public BoardDto getBoardCatalog(BoardDto board) {
    board.setThreads(threadFacade.getAllThreadsFromBoard(board));
    return board;
  }

  /**
   * Retrieves previews of all possible attachment categories which can be enabled on the board.
   *
   * @return previews of all attachment categories
   * @see AttachmentCategory.Preview
   */
  public Set<AttachmentCategory.Preview> getAttachmentCategories() {
    return Arrays.stream(AttachmentCategory.values())
        .map(AttachmentCategory::getPreview)
        .collect(Collectors.toSet());
  }

  /**
   * Checks if given MIME type (for example {@code image/jpeg}) is supported on given board.
   *
   * <p>MIME type is supported on board when it belongs to one of the attachment categories and that
   * category is enabled for given board.
   *
   * @param board board to check MIME type on
   * @param mimeType MIME type to check - case insensitive
   * @return true if MIME type is supported on given board, false otherwise
   * @see AttachmentCategory
   */
  public boolean isMimeTypeSupported(BoardDto board, String mimeType) {
    return board.getSettings().getAttachmentCategories().stream()
        .map(AttachmentCategory::getPreview)
        .flatMap(preview -> preview.getMimeTypes().stream())
        .anyMatch(mime -> mime.equalsIgnoreCase(mimeType));
  }

  /**
   * Edits a board.
   *
   * <p>Only board's name and its settings can be edited.
   *
   * @param board board to edit
   * @param editForm form with new values
   * @return edited board
   * @see BoardSettings
   */
  public BoardDto editBoard(BoardDto board, BoardForm editForm) {
    board.setName(editForm.getName());
    board.setSettings(editForm.getBoardSettingsForm());

    return toDto(boardService.updateBoard(toEntity(board)));
  }

  /** {@link BoardService#deleteBoard(Board)} */
  public void deleteBoard(BoardDto board) throws IOException {
    boardService.deleteBoard(toEntity(board));
  }
}
