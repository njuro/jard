package com.github.njuro.jard.board;

import com.github.njuro.jard.base.BaseRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends BaseRepository<Board> {

  Optional<Board> findByLabel(String label);

  @Query("select b.postCounter from Board b where b.label = :label")
  Long getPostCounter(@Param("label") String label);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update Board set postCounter = postCounter + 1 where label = :label")
  void increasePostNumber(@Param("label") String label);
}
