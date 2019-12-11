package com.github.njuro.jboard.thread.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jboard.common.RepositoryTest;
import com.github.njuro.jboard.thread.ThreadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ThreadRepositoryTest extends RepositoryTest {

  @Autowired private ThreadRepository threadRepository;

  @Test
  void testFindByBoardLabelAndOriginalPostPostNumber() {
    assertThat(threadRepository.findByBoardLabelAndOriginalPostPostNumber("r", 1L)).isPresent();

    assertThat(threadRepository.findByBoardLabelAndOriginalPostPostNumber("r", 5L)).isNotPresent();
  }
}
