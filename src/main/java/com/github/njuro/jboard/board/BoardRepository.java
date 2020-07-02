package com.github.njuro.jboard.board;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  Optional<Board> findByLabel(String label);

  @Query("select b.postCounter from Board b where b.label = :label")
  Long getPostCounter(@Param("label") String label);

  @Modifying
  @Query("update Board set postCounter = postCounter + 1 where label = :label")
  void increasePostNumber(@Param("label") String label);
}
