package com.github.njuro.jboard.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ThreadRepositoryTest extends RepositoryTest {

  @Autowired private ThreadRepository threadRepository;

  @Test
  void testFindByBoardLabelAndOriginalPostPostNumber() {
    assertThat(threadRepository.findByBoardLabelAndOriginalPostPostNumber("r", 1L))
        .isPresent();

    assertThat(threadRepository.findByBoardLabelAndOriginalPostPostNumber("r", 5L))
        .isNotPresent();
  }
}
