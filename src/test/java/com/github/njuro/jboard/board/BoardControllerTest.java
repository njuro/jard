package com.github.njuro.jboard.board;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jboard.common.Mappings;
import com.github.njuro.jboard.common.WebControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class BoardControllerTest extends WebControllerTest {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

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
    mockMvc
        .perform(
            put(Mappings.API_ROOT_BOARDS)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardForm)))
        .andExpect(status().isOk());
  }
}
