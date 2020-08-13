package com.github.njuro.jard.thread;

import com.github.njuro.jard.base.BaseRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends BaseRepository<Thread> {

  Optional<Thread> findByBoardLabelAndOriginalPostPostNumber(String label, Long postNumber);

  List<Thread> findByBoardIdOrderByStickiedDescLastBumpAtDesc(UUID boardId, Pageable pageRequest);

  Optional<Thread> findTopByBoardIdAndStickiedFalseOrderByLastBumpAtAsc(UUID boardId);

  Long countByBoardId(UUID boardId);
}
