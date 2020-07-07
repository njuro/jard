package com.github.njuro.jard.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

  @Mock private BoardRepository boardRepository;

  @InjectMocks private BoardService boardService;

  private Board boardN;
  private Board boardG;
  private Board boardInt;

  @BeforeEach
  void initBoards() {
    boardN = Board.builder().label("n").name("Automobiles").postCounter(11L).build();
    boardG = Board.builder().label("g").name("Technology").postCounter(25L).build();
    boardInt = Board.builder().label("int").name("International").postCounter(32L).build();
  }

  @Test
  void testSaveBoard() {
    when(boardRepository.save(boardG)).thenReturn(boardG);

    Board saved = boardService.saveBoard(boardG);
    assertThat(saved.getPostCounter()).isEqualTo(1L);
    assertThat(saved.getLabel()).isEqualTo(boardG.getLabel());

    verify(boardRepository).save(any(Board.class));
    verifyNoMoreInteractions(boardRepository);
  }

  @Test
  void testResolveBoard() {
    when(boardRepository.findByLabel("g")).thenReturn(Optional.of(boardG));

    Board result = boardService.resolveBoard("g");

    assertThat(result).isEqualToIgnoringNullFields(boardG);
    assertThatExceptionOfType(BoardNotFoundException.class)
        .isThrownBy(() -> boardService.resolveBoard("pol"));

    verify(boardRepository, times(2)).findByLabel(anyString());
    verifyNoMoreInteractions(boardRepository);
  }

  @Test
  void testGetAllBoards() {
    when(boardRepository.findAll()).thenReturn(Arrays.asList(boardG, boardInt, boardN));

    List<Board> result = boardService.getAllBoards();

    assertThat(result).containsExactlyInAnyOrder(boardInt, boardN, boardG);

    verify(boardRepository).findAll();
    verifyNoMoreInteractions(boardRepository);
  }

  @Test
  void testRegisterNewPost() {
    when(boardRepository.getPostCounter("g")).thenReturn(boardG.getPostCounter());
    doAnswer(
            invocation -> {
              boardG.setPostCounter(boardG.getPostCounter() + 1);
              return null;
            })
        .when(boardRepository)
        .increasePostNumber("g");

    Long originalPostCounter = boardG.getPostCounter();
    Long returnedPostNumber = boardService.registerNewPost(boardG);
    assertThat(returnedPostNumber).isEqualTo(originalPostCounter);
    assertThat(boardG.getPostCounter()).isEqualTo(originalPostCounter + 1);
    verify(boardRepository).increasePostNumber(anyString());
  }
}
