package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.user.UserAuthority;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

/**
 * Entity representing a board.
 *
 * <p>Board is like a forum, containing zero or more threads revolving around defined topic. The
 * maximum allowed amount of active threads is defined in {@link BoardSettings#threadLimit}. Once
 * this limit is surpassed, the stalest (the one with the least recent {@link Thread#lastBumpAt}
 * timestamp) is pruned in order to make place for the most recent one.
 *
 * <p>Authorized users can create, remove and edit boards. There are several optional features, that
 * can be enabled on given board, via its {@link #settings}.
 *
 * <p>When the board is deleted, all its active threads (which also means all the posts and
 * attachments) are deleted along with it.
 *
 * @see Thread
 * @see BoardSettings
 * @see UserAuthority#MANAGE_BOARDS
 */
@Entity
@Table(name = "boards")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
@SuppressWarnings("JavadocReference")
public class Board extends BaseEntity {

  private static final long serialVersionUID = -5779444327889471582L;

  /**
   * Short, unique public identifier/abbreviation of this board. Used in URLs belonging to board.
   */
  @Column(unique = true, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String label;

  /** Name of this board. Usually determines its topic. */
  @Basic @ToString.Include private String name;

  /**
   * Calculated property - how many pages of threads are currently active on this board.
   *
   * @see Constants#MAX_THREADS_PER_PAGE
   */
  @Formula(
      "(SELECT CEIL(COUNT(*)::float / "
          + MAX_THREADS_PER_PAGE
          + ") FROM threads t WHERE t.board_id = id)")
  private int pageCount;

  /**
   * Counter which increases by one every time new post is added to the board. Previous value is
   * then used as {@link Post#postNumber} for that new post.
   *
   * <p>Note that this counter never decreases, so it's denoting number of posts created on board
   * since its beginning.
   */
  @SuppressWarnings("JavadocReference")
  @Basic
  @Column(nullable = false)
  private Long postCounter;

  /**
   * Settings for this board, which can be edited by authorized user.
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

  /**
   * Before inserting to database, set creation date to current date and time if it is not set
   * already.
   */
  @PrePersist
  private void setCreatedAt() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }
}
