package com.github.njuro.jard.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import com.github.njuro.jard.common.WithMockUserAuthorities;
import com.github.njuro.jard.user.UserAuthority;
import java.util.List;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BoardIntegrationTest extends MockRequestTest {

  private static final String API_ROOT = Mappings.API_ROOT_BOARDS;

  @Autowired private BoardFacade boardFacade;

  private BoardForm boardForm;

  @BeforeEach
  public void setUp() {
    boardForm =
        BoardForm.builder()
            .name("Random")
            .label("r")
            .boardSettingsForm(
                BoardSettingsForm.builder()
                    .bumpLimit(Constants.MAX_BUMP_LIMIT)
                    .threadLimit(Constants.MAX_THREAD_LIMIT)
                    .attachmentCategories(Sets.newLinkedHashSet(AttachmentCategory.values()))
                    .nsfw(true)
                    .defaultPosterName("Anonymous")
                    .forceDefaultPosterName(true)
                    .build())
            .build();
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
  void testCreateBoard() throws Exception {
    performMockRequest(HttpMethod.PUT, API_ROOT, boardForm)
        .andExpect(status().isOk())
        .andExpect(content().json(toJson(boardForm.toBoard())));

    assertThat(boardFacade.resolveBoard(boardForm.getLabel())).isNotNull();
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
  void testGetAttachmentCategories() throws Exception {
    var result =
        performMockRequest(HttpMethod.GET, API_ROOT + "/attachment-categories", boardForm)
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponseCollection(result, Set.class, AttachmentCategory.Preview.class))
        .containsExactlyInAnyOrderElementsOf(boardFacade.getAttachmentCategories());
  }

  @Test
  void testGetAllBoards() throws Exception {
    boardFacade.createBoard(boardForm);

    var result =
        performMockRequest(HttpMethod.GET, API_ROOT)
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponseCollection(result, List.class, Board.class))
        .containsExactlyInAnyOrderElementsOf(boardFacade.getAllBoards());
  }

  @Test
  void testGetBoard() throws Exception {
    boardFacade.createBoard(boardForm);

    var result =
        performMockRequest(
                HttpMethod.GET,
                buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD, boardForm.getLabel()))
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponse(result, Board.class))
        .extracting(Board::getLabel)
        .isEqualTo(boardForm.getLabel());
  }

  @Test
  void testGetBoardCatalog() throws Exception {
    boardFacade.createBoard(boardForm);

    var result =
        performMockRequest(
                HttpMethod.GET,
                buildUri(
                    API_ROOT + Mappings.PATH_VARIABLE_BOARD + "/catalog", boardForm.getLabel()))
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponse(result, Board.class))
        .extracting(Board::getLabel)
        .isEqualTo(boardForm.getLabel());
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
  void testEditBoard() throws Exception {
    boardFacade.createBoard(boardForm);

    assertThat(boardFacade.resolveBoard(boardForm.getLabel()))
        .isNotNull()
        .extracting(Board::getName)
        .isEqualTo(boardForm.getName());

    String newName = "Technology";
    boardForm.setName(newName);
    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD + "/edit", boardForm.getLabel()),
            boardForm)
        .andExpect(status().isOk());

    assertThat(boardFacade.resolveBoard(boardForm.getLabel()))
        .isNotNull()
        .extracting(Board::getName)
        .isEqualTo(newName);
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
  void testDeleteBoard() throws Exception {
    boardFacade.createBoard(boardForm);

    assertThat(boardFacade.resolveBoard(boardForm.getLabel())).isNotNull();

    performMockRequest(
            HttpMethod.DELETE,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD, boardForm.getLabel()))
        .andExpect(status().isOk());

    assertThatThrownBy(() -> boardFacade.resolveBoard(boardForm.getLabel()))
        .isInstanceOf(BoardNotFoundException.class);
  }
}
