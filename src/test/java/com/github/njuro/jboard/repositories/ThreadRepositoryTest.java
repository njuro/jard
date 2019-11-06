package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.Thread;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadRepositoryTest extends RepositoryTest {

    @Autowired
    private ThreadRepository threadRepository;

    @Test
    void testFindByBoardLabel() {
        assertThat(threadRepository
                .findByBoardLabelOrderByStickiedDescLastReplyAtDesc("r"))
                .extracting(Thread::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void testFindByBoardLabelAndOriginalPostPostNumber() {
        assertThat(threadRepository
                .findByBoardLabelAndOriginalPostPostNumber("r", 2L)).isPresent();

        assertThat(threadRepository
                .findByBoardLabelAndOriginalPostPostNumber("r", 3L)).isNotPresent();
    }


}
