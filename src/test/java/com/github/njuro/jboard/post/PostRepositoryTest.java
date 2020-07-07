package com.github.njuro.jboard.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jboard.common.RepositoryTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostRepositoryTest extends RepositoryTest {

  @Autowired private PostRepository postRepository;

  private final UUID THREAD_ID = UUID.fromString("adf57470-ab38-4771-b709-f752e36f7c4f");
  private final UUID ORIGINAL_POST_ID = UUID.fromString("71fd63b2-3b2f-4512-a47e-0b4226607ef2");

  @Test
  void testFindByThreadBoardLabelAndPostNumber() {
    assertThat(postRepository.findByThreadBoardLabelAndPostNumber("r", 1L))
        .isPresent()
        .hasValueSatisfying(post -> post.getName().equals("Admin"));
    assertThat(postRepository.findByThreadBoardLabelAndPostNumber("r", 10L)).isNotPresent();
    assertThat(postRepository.findByThreadBoardLabelAndPostNumber("a", 1L)).isNotPresent();
  }

  @Test
  public void testFindByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc() {
    assertThat(
            postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(THREAD_ID, 2L))
        .extracting(Post::getPostNumber)
        .containsExactly(5L, 6L);
  }

  @Test
  void testFindByThreadIdAndIdIsNotOrderByCreatedAtAsc() {
    assertThat(
            postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(THREAD_ID, ORIGINAL_POST_ID))
        .extracting(Post::getPostNumber)
        .containsExactly(2L, 5L, 6L);
  }

  @Test
  void testFindTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc() {
    assertThat(
            postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(
                THREAD_ID, ORIGINAL_POST_ID))
        .extracting(Post::getPostNumber)
        .containsExactly(6L, 5L, 2L);
  }

  @Test
  void testCountByThreadId() {
    assertThat(postRepository.countByThreadId(THREAD_ID)).isEqualTo(4L);
  }
}
