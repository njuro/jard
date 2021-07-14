package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.board.dto.BoardForm;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.thread.dto.ThreadDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("MVCPathVariableInspection")
@RestController
@RequestMapping(Mappings.API_ROOT_BOARDS)
@Slf4j
public class BoardRestController {

  private final BoardFacade boardFacade;

  @Autowired
  public BoardRestController(BoardFacade boardFacade) {
    this.boardFacade = boardFacade;
  }

  @PostMapping
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public ResponseEntity<BoardDto> createBoard(@RequestBody @Valid BoardForm boardForm) {
    return ResponseEntity.status(HttpStatus.CREATED).body(boardFacade.createBoard(boardForm));
  }

  @GetMapping("/attachment-categories")
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public Set<AttachmentCategory.Preview> getBoardAttachmentCategories() {
    return boardFacade.getAttachmentCategories();
  }

  @GetMapping
  @FieldFilterSetting(className = BoardDto.class, fields = "threads")
  public List<BoardDto> getAllBoards() {
    return boardFacade.getAllBoards();
  }

  @GetMapping(Mappings.PATH_VARIABLE_BOARD)
  @FieldFilterSetting(className = ThreadDto.class, fields = "board")
  @DynamicFilter(SensitiveDataFilter.class)
  public BoardDto getBoard(
      BoardDto board, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
    return boardFacade.getBoard(board, PageRequest.of(page - 1, MAX_THREADS_PER_PAGE));
  }

  @GetMapping(Mappings.PATH_VARIABLE_BOARD + "/catalog")
  @FieldFilterSetting(className = ThreadDto.class, fields = "board")
  @DynamicFilter(SensitiveDataFilter.class)
  public BoardDto getBoardCatalog(BoardDto board) {
    return boardFacade.getBoardCatalog(board);
  }

  @PutMapping(Mappings.PATH_VARIABLE_BOARD)
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public BoardDto editBoard(BoardDto oldBoard, @RequestBody @Valid BoardForm boardForm) {
    return boardFacade.editBoard(oldBoard, boardForm);
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_BOARD)
  @HasAuthorities(UserAuthority.MANAGE_BOARDS)
  public ResponseEntity<Object> deleteBoard(BoardDto board) {
    try {
      boardFacade.deleteBoard(board);
      return ResponseEntity.ok().build();
    } catch (IOException ex) {
      log.error("Deleting board failed", ex);
      return ResponseEntity.badRequest().body("Deleting board failed");
    }
  }
}
