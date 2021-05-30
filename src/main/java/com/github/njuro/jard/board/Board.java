package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.post.Post;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

/** Entity representing a board (usually with a set topic) which can contain threads */
@Entity
@Table(name = "boards")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Board extends BaseEntity {

  private static final long serialVersionUID = -5779444327889471582L;

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
  private Long postCounter;

  /**
   * The editable settings of this board.
   *
   * @see BoardSettings
   */
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "board", fetch = FetchType.LAZY, optional = false)
  @Fetch(FetchMode.JOIN)
  @Builder.Default
  private BoardSettings settings = new BoardSettings();

  /** Date and time when this board was created. */
  @Column(nullable = false)
  private OffsetDateTime createdAt;

  /** Before inserting to database, set creation date to current date and time if it is null. */
  @PrePersist
  private void setCreatedAt() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }
}
