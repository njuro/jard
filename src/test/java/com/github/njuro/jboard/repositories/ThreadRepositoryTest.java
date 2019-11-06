package com.github.njuro.jboard.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadRepositoryTest extends RepositoryTest {

    @Autowired
    private ThreadRepository threadRepository;

    @Test
    void testFindByBoardLabelAndOriginalPostPostNumber() {
        assertThat(threadRepository
                .findByBoardLabelAndOriginalPostPostNumber("r", 2L)).isPresent();

        assertThat(threadRepository
                .findByBoardLabelAndOriginalPostPostNumber("r", 3L)).isNotPresent();
    }


}
