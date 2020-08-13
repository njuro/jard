package com.github.njuro.jard.post;

import com.github.njuro.jard.base.BaseRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends BaseRepository<Post> {

  Optional<Post> findByThreadBoardLabelAndPostNumber(String label, Long postNumber);

  List<Post> findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
      UUID threadId, Long postNumber);

  List<Post> findByThreadIdAndIdIsNotOrderByCreatedAtAsc(UUID threadId, UUID originalPostId);

  List<Post> findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(UUID threadId, UUID originalPostId);

  Long countByThreadId(UUID threadId);
}
