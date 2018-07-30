package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.BoardNotFoundException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.repositories.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BoardServiceTest extends ServiceTest {

    @MockBean
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

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
    void testResolveBoard() {
        when(boardRepository.findByLabel("g")).thenReturn(Optional.of(boardG));

        Board result = boardService.resolveBoard("g");

        assertThat(result).isEqualToIgnoringNullFields(boardG);
        assertThatExceptionOfType(BoardNotFoundException.class).isThrownBy(() -> boardService.resolveBoard("pol"));

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
}
