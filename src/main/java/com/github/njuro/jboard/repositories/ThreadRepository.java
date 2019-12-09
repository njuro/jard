package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.Thread;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {

  Optional<Thread> findByBoardLabelAndOriginalPostPostNumber(String label, Long postNumber);
}
