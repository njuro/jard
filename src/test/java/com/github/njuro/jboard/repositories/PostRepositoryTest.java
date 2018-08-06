package com.github.njuro.jboard.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PostRepositoryTest extends RepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void testFindByThreadBoardLabelAndPostNumber() {
        assertThat(postRepository.findByThreadBoardLabelAndPostNumber("r", 1L)).isPresent()
                .hasValueSatisfying(post -> post.getName().equals("Admin"));
        assertThat(postRepository.findByThreadBoardLabelAndPostNumber("r", 10L)).isNotPresent();
        assertThat(postRepository.findByThreadBoardLabelAndPostNumber("a", 1L)).isNotPresent();
    }
}
