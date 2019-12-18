package com.github.njuro.jboard.board;

import static com.github.njuro.jboard.common.Constants.MAX_BOARD_LABEL_LENGTH;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jboard.common.ControllerTest;
import com.github.njuro.jboard.common.Mappings;
import java.util.Locale;
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

    boardForm.setLabel(RandomStringUtils.random(MAX_BOARD_LABEL_LENGTH + 1));
    attemptToCreateBoard("validation.board.label.length", MAX_BOARD_LABEL_LENGTH);
    boardForm.setLabel(null);
    attemptToCreateBoard("validation.board.label.null");
  }

  private void attemptToCreateBoard(String expectedMessageCode, Object... parameters)
      throws Exception {
    performMockRequest(HttpMethod.PUT, Mappings.API_ROOT_BOARDS, boardForm)
        .andExpect(
            validationError(
                messageSource.getMessage(expectedMessageCode, parameters, Locale.getDefault())));
    setUp();
  }
}
