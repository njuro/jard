package com.github.njuro.jboard.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jboard.models.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardRepositoryTest extends RepositoryTest {

  @Autowired private BoardRepository boardRepository;

  @Test
  void testFindByLabel() {
    assertThat(this.boardRepository.findByLabel("fit"))
        .hasValueSatisfying(board -> board.getName().equals("Fitness"));
    assertThat(this.boardRepository.findByLabel("b")).isNotPresent();
  }

  @Test
  void testPostCounter() {
    final Board board =
        this.boardRepository.findByLabel("fit").orElseThrow(IllegalStateException::new);
    final long postCounterBefore = board.getPostCounter();
    this.boardRepository.increasePostNumber(board.getLabel());
    final long postCounterAfter = this.boardRepository.getPostCounter(board.getLabel());

    assertThat(postCounterAfter).isEqualTo(postCounterBefore + 1);
  }
}
