package com.github.njuro.jboard.services;

import com.github.njuro.jboard.MockDatabaseTest;
import com.github.njuro.jboard.models.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class BoardRepositoryTest extends MockDatabaseTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    void testFindByLabel() {
        for (Board board : boardRepository.findAll()) {
            System.out.println(board.getLabel());
        }
        assertThat(boardRepository.findByLabel("fit")).hasValueSatisfying(board -> board.getName().equals("Fitness"));
        assertThat(boardRepository.findByLabel("b")).isNotPresent();
    }

    @Test
    void testPostCounter() {
        Board board = boardRepository.findByLabel("fit").orElseThrow(IllegalStateException::new);
        long postCounterBefore = board.getPostCounter();
        boardRepository.increasePostNumber(board.getLabel());
        long postCounterAfter = boardRepository.getPostCounter(board.getLabel());

        assertThat(postCounterAfter).isEqualTo(postCounterBefore + 1);
    }
}
