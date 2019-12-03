package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByThreadBoardLabelAndPostNumber(String label, Long postNumber);

  List<Post> findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
      Long threadId, Long postNumber);
}
