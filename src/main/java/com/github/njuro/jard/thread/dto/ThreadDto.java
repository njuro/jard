package com.github.njuro.jard.thread.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jard.base.BaseDto;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.thread.Thread;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** DTO for {@link Thread}. */
@SuppressWarnings("JavadocReference")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class ThreadDto extends BaseDto {
  @Serial private static final long serialVersionUID = -2107575988634432522L;

  /** {@link Thread#getThreadNumber()} */
  @EqualsAndHashCode.Include private Long threadNumber;

  /** {@link Thread#subject} */
  private String subject;

  /** {@link Thread#locked} */
  private boolean locked;

  /** {@link Thread#stickied} */
  private boolean stickied;

  /** {@link Thread#createdAt} */
  private OffsetDateTime createdAt;

  /** {@link Thread#lastReplyAt} */
  private OffsetDateTime lastReplyAt;

  /** {@link Thread#lastBumpAt} */
  private OffsetDateTime lastBumpAt;

  /** {@link Thread#board} */
  @EqualsAndHashCode.Include private BoardDto board;

  /** {@link Thread#originalPost} */
  @ToString.Include
  @JsonIgnoreProperties(value = "thread", allowSetters = true)
  private PostDto originalPost;

  /** {@link Thread#statistics} */
  private ThreadStatisticsDto statistics;

  /**
   * (Sub)collection of replies for this thread. Fetched by different services based on various
   * parameters (depending on need of the given API).
   *
   * @see PostDto
   */
  @Transient
  @JsonIgnoreProperties(value = "thread", allowSetters = true)
  private List<PostDto> replies;

  public void toggleLock() {
    locked = !locked;
  }

  public void toggleSticky() {
    stickied = !stickied;
  }
}
