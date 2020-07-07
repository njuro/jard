package com.github.njuro.jard.thread.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jard.common.RepositoryTest;
import com.github.njuro.jard.thread.ThreadRepository;
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
