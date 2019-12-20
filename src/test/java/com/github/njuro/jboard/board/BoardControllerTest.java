package com.github.njuro.jboard.board;

import static com.github.njuro.jboard.common.Constants.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BOARD_NAME_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BUMP_LIMIT;
import static com.github.njuro.jboard.common.Constants.MAX_THREAD_LIMIT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jboard.common.ControllerTest;
import com.github.njuro.jboard.common.Mappings;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;

public class BoardControllerTest extends ControllerTest {

  @MockBean private BoardFacade boardFacade;

  private BoardForm boardForm;

  @BeforeEach
  public void setUp() {
    boardForm =
        BoardForm.builder()
            .label("r")
            .name("Random")
            .attachmentType(BoardAttachmentType.IMAGE)
            .nsfw(true)
            .bumpLimit(350)
            .threadLimit(100)
            .build();
  }

  @Test
  public void testCreateBoard() throws Exception {
    performMockRequest(HttpMethod.PUT, Mappings.API_ROOT_BOARDS, boardForm)
        .andExpect(status().isOk());

    boardForm.setLabel(" ");
    expectValidationErrors("label");
    boardForm.setLabel(RandomStringUtils.random(MAX_BOARD_LABEL_LENGTH + 1));
    expectValidationErrors("label");

    boardForm.setName(" ");
    expectValidationErrors("name");
    boardForm.setName(RandomStringUtils.random(MAX_BOARD_NAME_LENGTH + 1));
    expectValidationErrors("name");

    boardForm.setAttachmentType(null);
    expectValidationErrors("attachmentType");

    boardForm.setThreadLimit(0);
    expectValidationErrors("threadLimit");
    boardForm.setThreadLimit(MAX_THREAD_LIMIT + 1);
    expectValidationErrors("threadLimit");

    boardForm.setBumpLimit(-1);
    expectValidationErrors("bumpLimit");
    boardForm.setBumpLimit(MAX_BUMP_LIMIT + 1);
    expectValidationErrors("bumpLimit");
  }

  private void expectValidationErrors(String... expectedFieldErrors) throws Exception {
    performMockRequest(HttpMethod.PUT, Mappings.API_ROOT_BOARDS, boardForm)
        .andExpect(validationError(expectedFieldErrors));
    setUp();
  }
}
