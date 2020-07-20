package com.github.njuro.jard.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class BoardIntegrationTest extends MockRequestTest {

  private static final String API_ROOT = Mappings.API_ROOT_BOARDS;

  @Autowired private BoardRepository boardRepository;
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
  @WithMockUser(authorities = "MANAGE_BOARDS")
  public void testCreateBoard() throws Exception {
    performMockRequest(HttpMethod.PUT, API_ROOT, boardForm)
        .andExpect(status().isOk())
        .andExpect(content().json(toJson(boardForm.toBoard())));

    assertThat(boardRepository.findByLabel(boardForm.getLabel())).isPresent();
  }

  @Test
  @WithMockUser(authorities = "MANAGE_BOARDS")
  public void testGetAttachmentCategories() throws Exception {
    performMockRequest(HttpMethod.GET, API_ROOT + "/attachment-categories", boardForm)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());
  }

  @Test
  public void testGetAllBoards() throws Exception {
    boardFacade.createBoard(boardForm);

    performMockRequest(HttpMethod.GET, API_ROOT)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());
  }

  @Test
  public void testGetBoard() throws Exception {
    boardFacade.createBoard(boardForm);

    performMockRequest(
            HttpMethod.GET, buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD, boardForm.getLabel()))
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());
  }

  @Test
  public void testGetBoardCatalog() throws Exception {
    boardFacade.createBoard(boardForm);

    performMockRequest(
            HttpMethod.GET,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD + "/catalog", boardForm.getLabel()))
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());
  }

  @Test
  @WithMockUser(authorities = "MANAGE_BOARDS")
  public void testEditBoard() throws Exception {
    boardFacade.createBoard(boardForm);

    assertThat(boardRepository.findByLabel(boardForm.getLabel()))
        .isPresent()
        .hasValueSatisfying(board -> assertThat(board.getName()).isEqualTo(boardForm.getName()));

    String newName = "Technology";
    boardForm.setName(newName);
    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD + "/edit", boardForm.getLabel()),
            boardForm)
        .andExpect(status().isOk());

    assertThat(boardRepository.findByLabel(boardForm.getLabel()))
        .isPresent()
        .hasValueSatisfying(board -> assertThat(board.getName()).isEqualTo(newName));
  }

  @Test
  @WithMockUser(authorities = "MANAGE_BOARDS")
  public void testDeleteBoard() throws Exception {
    boardFacade.createBoard(boardForm);

    assertThat(boardRepository.findByLabel(boardForm.getLabel())).isPresent();

    performMockRequest(
            HttpMethod.DELETE,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BOARD, boardForm.getLabel()))
        .andExpect(status().isOk());

    assertThat(boardRepository.findByLabel(boardForm.getLabel())).isNotPresent();
  }
}
