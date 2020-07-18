package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.user.UserAuthority;
import com.github.njuro.jard.utils.SensitiveDataFilter;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Mappings.API_ROOT_BOARDS)
@Slf4j
public class BoardRestController {

  private final BoardFacade boardFacade;

  @Autowired
  public BoardRestController(BoardFacade boardFacade) {
    this.boardFacade = boardFacade;
  }

  @PutMapping
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public Board createBoard(@RequestBody @Valid BoardForm boardForm) {
    return boardFacade.createBoard(boardForm);
  }

  @GetMapping("/attachment-categories")
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public Set<AttachmentCategory.Preview> getBoardAttachmentCategories() {
    return boardFacade.getAttachmentCategories();
  }

  @GetMapping
  @FieldFilterSetting(className = Board.class, fields = "threads")
  public List<Board> getAllBoards() {
    return boardFacade.getAllBoards();
  }

  @GetMapping(Mappings.PATH_VARIABLE_BOARD)
  @FieldFilterSetting(className = Thread.class, fields = "board")
  @DynamicFilter(SensitiveDataFilter.class)
  public Board getBoard(
      Board board, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
    return boardFacade.getBoard(board, PageRequest.of(page - 1, MAX_THREADS_PER_PAGE));
  }

  @GetMapping(Mappings.PATH_VARIABLE_BOARD + "/catalog")
  @FieldFilterSetting(className = Thread.class, fields = "board")
  @DynamicFilter(SensitiveDataFilter.class)
  public Board getBoardCatalog(Board board) {
    return boardFacade.getBoardCatalog(board);
  }

  @PostMapping(Mappings.PATH_VARIABLE_BOARD + "/edit")
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public Board editBoard(Board oldBoard, @RequestBody @Valid BoardForm boardForm) {
    return boardFacade.editBoard(oldBoard, boardForm);
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_BOARD)
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public ResponseEntity<Object> deleteBoard(Board board) {
    try {
      boardFacade.deleteBoard(board);
      return ResponseEntity.ok().build();
    } catch (IOException ex) {
      log.error("Deleting board failed", ex);
      return ResponseEntity.badRequest().body("Deleting board failed");
    }
  }
}
