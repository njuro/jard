package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    Optional<Thread> findByBoardLabelAndOriginalPostPostNumber(String label, Long postNumber);

}
