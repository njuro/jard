package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.Thread;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

/** Entity representing a board (usually with a set topic) which can contain threads */
@Entity
@Table(name = "boards")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Board implements Serializable {

  private static final long serialVersionUID = -5779444327889471582L;

  /** Unique identifier of this board. */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  @JsonIgnore
  private UUID id;

  /** Short public identifier of this board - must be unique. Example: {@code fit, pol...} */
  @Column(unique = true, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String label;

  /** Full name of this board. Should be related to its topic. */
  @Basic @ToString.Include private String name;

  /**
   * Calculated property - how many pages of threads are currently active on this board. Max size of
   * each page is determined by {@link Constants#MAX_THREADS_PER_PAGE}
   */
  @Formula(
      "(SELECT CEIL(COUNT(*) / " + MAX_THREADS_PER_PAGE + ") FROM threads t WHERE t.board_id = id)")
  private int pageCount;

  /** The {@link Post#postNumber} of next post on this board. */
  @SuppressWarnings("JavadocReference")
  @Basic
  @Column(nullable = false)
  @JsonIgnore
  private Long postCounter;

  /**
   * The editable settings of this board.
   *
   * @see BoardSettings
   */
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "board", optional = false)
  @Builder.Default
  private BoardSettings settings = new BoardSettings();

  /** Date and time when this board was created. */
  @Column(nullable = false)
  private OffsetDateTime createdAt;

  /** Before inserting to database, set creation date to current date and time. */
  @PrePersist
  private void setCreatedAt() {
    createdAt = OffsetDateTime.now();
  }

  /**
   * (Sub)collection of active threads on this board. Fetched by different services when needed.
   *
   * @see Thread
   */
  @Transient
  @JsonIgnoreProperties("board")
  private List<Thread> threads;
}
