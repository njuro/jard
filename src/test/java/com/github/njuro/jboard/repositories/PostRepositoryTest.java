package com.github.njuro.jboard.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostRepositoryTest extends RepositoryTest {

  @Autowired private PostRepository postRepository;

  @Test
  void testFindByThreadBoardLabelAndPostNumber() {
    assertThat(this.postRepository.findByThreadBoardLabelAndPostNumber("r", 1L))
        .isPresent()
        .hasValueSatisfying(post -> post.getName().equals("Admin"));
    assertThat(this.postRepository.findByThreadBoardLabelAndPostNumber("r", 10L)).isNotPresent();
    assertThat(this.postRepository.findByThreadBoardLabelAndPostNumber("a", 1L)).isNotPresent();
  }
}
