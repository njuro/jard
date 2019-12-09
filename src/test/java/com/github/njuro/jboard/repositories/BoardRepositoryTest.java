package com.github.njuro.jboard.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jboard.models.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardRepositoryTest extends RepositoryTest {

  @Autowired private BoardRepository boardRepository;

  @Test
  void testFindByLabel() {
    assertThat(boardRepository.findByLabel("fit"))
        .hasValueSatisfying(board -> board.getName().equals("Fitness"));
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
