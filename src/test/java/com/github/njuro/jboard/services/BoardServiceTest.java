package com.github.njuro.jboard.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.njuro.jboard.exceptions.BoardNotFoundException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.repositories.BoardRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class BoardServiceTest extends ServiceTest {

  @MockBean private BoardRepository boardRepository;

  @Autowired private BoardService boardService;

  private Board boardN;
  private Board boardG;
  private Board boardInt;

  @BeforeEach
  void initBoards() {
    this.boardN = Board.builder().label("n").name("Automobiles").postCounter(11L).build();
    this.boardG = Board.builder().label("g").name("Technology").postCounter(25L).build();
    this.boardInt = Board.builder().label("int").name("International").postCounter(32L).build();
  }

  @Test
  void testResolveBoard() {
    when(this.boardRepository.findByLabel("g")).thenReturn(Optional.of(this.boardG));

    final Board result = this.boardService.resolveBoard("g");

    assertThat(result).isEqualToIgnoringNullFields(this.boardG);
    assertThatExceptionOfType(BoardNotFoundException.class)
        .isThrownBy(() -> this.boardService.resolveBoard("pol"));

    verify(this.boardRepository, times(2)).findByLabel(anyString());
    verifyNoMoreInteractions(this.boardRepository);
  }

  @Test
  void testGetAllBoards() {
    when(this.boardRepository.findAll())
        .thenReturn(Arrays.asList(this.boardG, this.boardInt, this.boardN));

    final List<Board> result = this.boardService.getAllBoards();

    assertThat(result).containsExactlyInAnyOrder(this.boardInt, this.boardN, this.boardG);

    verify(this.boardRepository).findAll();
    verifyNoMoreInteractions(this.boardRepository);
  }
}
