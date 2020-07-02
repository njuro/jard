package com.github.njuro.jboard.thread;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, UUID> {

  Optional<Thread> findByBoardLabelAndOriginalPostPostNumber(String label, Long postNumber);

  List<Thread> findByBoardIdOrderByStickiedDescLastReplyAtDesc(UUID boardId, Pageable pageRequest);

  Optional<Thread> findTopByBoardIdAndStickiedFalseOrderByLastReplyAtAsc(UUID boardId);

  Long countByBoardId(UUID boardId);
}
