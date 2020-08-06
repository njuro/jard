package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.InputConstraints.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.njuro.jard.common.ControllerTest;
import com.github.njuro.jard.common.EntityUtils;
import com.github.njuro.jard.common.Mappings;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;

class BoardControllerTest extends ControllerTest {

  private static final String API_ROOT = Mappings.API_ROOT_BOARDS;

  @MockBean private BoardFacade boardFacade;

  private BoardForm boardForm;

  @Captor private ArgumentCaptor<BoardForm> boardFormCaptor;
  @Captor private ArgumentCaptor<Board> boardCaptor;
  @Captor private ArgumentCaptor<Pageable> pageableCaptor;

  @BeforeEach
  public void setUp() {
    boardForm =
        BoardForm.builder()
            .label("r")
            .name("Random")
            .boardSettingsForm(
                BoardSettingsForm.builder().nsfw(true).bumpLimit(350).threadLimit(100).build())
            .build();
  }

  @Test
  void testCreateBoard() throws Exception {
    when(boardFacade.createBoard(boardForm)).thenReturn(boardForm.toBoard());

    performMockRequest(HttpMethod.PUT, API_ROOT, boardForm)
        .andExpect(status().isOk())
        .andExpect(content().json(toJson(boardForm.toBoard())));

    verify(boardFacade).createBoard(boardFormCaptor.capture());
    assertThat(boardFormCaptor.getValue()).isEqualToComparingFieldByField(boardForm);

    boardForm.setLabel(" ");
    expectValidationErrors("label");
    boardForm.setLabel(RandomStringUtils.random(MAX_BOARD_LABEL_LENGTH + 1));
    expectValidationErrors("label");

    boardForm.setName(" ");
    expectValidationErrors("name");
    boardForm.setName(RandomStringUtils.random(MAX_BOARD_NAME_LENGTH + 1));
    expectValidationErrors("name");

    boardForm.getBoardSettingsForm().setThreadLimit(0);
    expectValidationErrors("boardSettingsForm.threadLimit");
    boardForm.getBoardSettingsForm().setThreadLimit(MAX_THREAD_LIMIT + 1);
    expectValidationErrors("boardSettingsForm.threadLimit");

    boardForm.getBoardSettingsForm().setBumpLimit(-1);
    expectValidationErrors("boardSettingsForm.bumpLimit");
    boardForm.getBoardSettingsForm().setBumpLimit(MAX_BUMP_LIMIT + 1);
    expectValidationErrors("boardSettingsForm.bumpLimit");
  }

  @Test
  void testBoardTypes() throws Exception {
    when(boardFacade.getAttachmentCategories()).thenCallRealMethod();
    performMockRequest(HttpMethod.GET, API_ROOT + "/attachment-categories")
        .andExpect(status().isOk())
        .andExpect(content().json(toJson(boardFacade.getAttachmentCategories())));
  }

  @Test
  void testGetAllBoards() throws Exception {
    when(boardFacade.getAllBoards()).thenReturn(Collections.singletonList(boardForm.toBoard()));
    performMockRequest(HttpMethod.GET, API_ROOT)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name").exists())
        .andExpect(jsonPath("$[*].threads").doesNotExist());
  }

  @Test
  void testGetBoard() throws Exception {
    when(boardFacade.resolveBoard(boardForm.getLabel())).thenReturn(boardForm.toBoard());
    when(boardFacade.getBoard(any(Board.class), any(Pageable.class)))
        .thenReturn(EntityUtils.randomBoard(1));
    performMockRequest(
            HttpMethod.GET,
            buildUri(
                API_ROOT + Mappings.PATH_VARIABLE_BOARD + "?page=2&size=10", boardForm.getLabel()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.threads[*]").exists())
        .andExpect(jsonPath("$.threads[*].board").doesNotExist())
        .andExpect(jsonPath("$.threads[*].originalPost.ip").doesNotExist());

    verify(boardFacade).getBoard(boardCaptor.capture(), pageableCaptor.capture());

    assertThat(boardCaptor.getValue()).isEqualToComparingFieldByField(boardForm.toBoard());
    assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
    assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
  }

  @Test
  void testGetBoardCatalog() throws Exception {
    when(boardFacade.resolveBoard(boardForm.getLabel())).thenReturn(boardForm.toBoard());
    when(boardFacade.getBoardCatalog(any(Board.class))).thenReturn(EntityUtils.randomBoard(1));

    performMockRequest(
            HttpMethod.GET,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD + "/catalog", boardForm.getLabel()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.threads[*]").exists())
        .andExpect(jsonPath("$.threads[*].board").doesNotExist())
        .andExpect(jsonPath("$.threads[*].originalPost.ip").doesNotExist());

    verify(boardFacade).getBoardCatalog(boardCaptor.capture());
    assertThat(boardCaptor.getValue()).isEqualToComparingFieldByField(boardForm.toBoard());
  }

  @Test
  void testEditBoard() throws Exception {
    when(boardFacade.resolveBoard(boardForm.getLabel())).thenReturn(boardForm.toBoard());

    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD + "/edit", boardForm.getLabel()),
            boardForm)
        .andExpect(status().isOk());

    verify(boardFacade).editBoard(boardCaptor.capture(), boardFormCaptor.capture());
    assertThat(boardCaptor.getValue()).isEqualToComparingFieldByField(boardForm.toBoard());
    assertThat(boardFormCaptor.getValue()).isEqualToComparingFieldByField(boardForm);
  }

  @Test
  void testDeleteBoard() throws Exception {
    when(boardFacade.resolveBoard(boardForm.getLabel())).thenReturn(boardForm.toBoard());

    performMockRequest(
            HttpMethod.DELETE,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD, boardForm.getLabel()))
        .andExpect(status().isOk());

    verify(boardFacade).deleteBoard(boardCaptor.capture());
    assertThat(boardCaptor.getValue()).isEqualToComparingFieldByField(boardForm.toBoard());
  }

  private void expectValidationErrors(String... expectedFieldErrors) throws Exception {
    performMockRequest(HttpMethod.PUT, API_ROOT, boardForm)
        .andExpect(validationError(expectedFieldErrors));
    setUp();
  }
}
