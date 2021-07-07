package com.github.njuro.jard.board.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jard.base.BaseDto;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.thread.dto.ThreadDto;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** DTO for {@link Board}. */
@SuppressWarnings("JavadocReference")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class BoardDto extends BaseDto {

  private static final long serialVersionUID = -1195236367042529548L;

  /** {@link Board#label} */
  @EqualsAndHashCode.Include @ToString.Include private String label;

  /** {@link Board#name} */
  @ToString.Include private String name;

  /** {@link Board#pageCount} */
  private int pageCount;

  /** {@link Board#settings} */
  @Builder.Default private BoardSettingsDto settings = new BoardSettingsDto();

  /** {@link Board#createdAt} */
  private OffsetDateTime createdAt;

  /**
   * (Sub)collection of active threads on this board. Fetched by different services based on various
   * parameters (depending on need of the given API).
   *
   * @see ThreadDto
   */
  @JsonIgnoreProperties(value = "board", allowSetters = true)
  private List<ThreadDto> threads;
}
