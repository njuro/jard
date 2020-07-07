package com.github.njuro.jard.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.njuro.jard.post.PostService;
import com.github.njuro.jard.thread.ThreadService;
import com.github.njuro.jard.utils.validation.FormValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardFacadeTest {

  @Mock private BoardService boardService;

  @Mock private ThreadService threadService;

  @Mock private PostService postService;

  @InjectMocks private BoardFacade boardFacade;

  private BoardForm boardForm;

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
  public void testCreateBoard() {
    when(boardService.doesBoardExist(boardForm.getLabel())).thenReturn(false);
    when(boardService.saveBoard(any(Board.class))).thenAnswer(a -> a.getArgument(0));

    Board saved = boardFacade.createBoard(boardForm);
    assertThat(saved)
        .isEqualToIgnoringGivenFields(
            boardForm, "id", "settings", "pageCount", "postCounter", "threads");
  }

  @Test
  public void testCreateBoardAlreadyExists() {
    when(boardService.doesBoardExist(boardForm.getLabel())).thenReturn(true);

    assertThatExceptionOfType(FormValidationException.class)
        .isThrownBy(() -> boardFacade.createBoard(boardForm))
        .withMessageContaining("label already exists");
  }
}
