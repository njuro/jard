package com.github.njuro.jboard.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

  Optional<Post> findByThreadBoardLabelAndPostNumber(String label, Long postNumber);

  List<Post> findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
      UUID threadId, Long postNumber);

  List<Post> findByThreadIdAndIdIsNotOrderByCreatedAtAsc(UUID threadId, UUID originalPostId);

  List<Post> findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(UUID threadId, UUID originalPostId);

  Long countByThreadId(UUID threadId);
}
