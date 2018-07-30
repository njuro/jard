package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByLabel(String label);

    @Query("select b.postCounter from Board b where b.label = :label")
    Long getPostCounter(@Param("label") String label);

    @Modifying
    @Query("update Board set postCounter = postCounter + 1 where label = :label")
    void increasePostNumber(@Param("label") String label);
}
