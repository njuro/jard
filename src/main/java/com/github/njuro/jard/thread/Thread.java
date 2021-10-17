package com.github.njuro.jard.thread;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.board.BoardSettings;
import com.github.njuro.jard.post.Post;
import java.io.Serial;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Entity representing a thread on board.
 *
 * <p>Thread consists of mandatory first post (also called OP, or Original Post) and zero or more
 * replies.
 *
 * @see Board
 * @see Post
 */
@Entity
@Table(name = "threads")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
@SuppressWarnings("JavadocReference")
public class Thread extends BaseEntity {

  @Serial private static final long serialVersionUID = -7257462911390779498L;

  /** (Optional) subject of this thread. */
  @Basic private String subject;

  /** If true, this thread is locked (new replies cannot be submitted). */
  @Basic private boolean locked;

  /**
   * If true, this thread is stickied (always on top of the board and won't be automatically
   * deleted).
   */
  @Basic private boolean stickied;

  /** Date and time when this thread was created. */
  @Column(nullable = false)
  private OffsetDateTime createdAt;

  /**
   * Date and time of the last reply to this thread. Initially it is the same as {@link #createdAt}.
   */
  @Column(nullable = false)
  private OffsetDateTime lastReplyAt;

  /**
   * Date and time when this thread was last bumped.
   *
   * <p>Last bump timestamp is important when evaluating which thread will be automatically deleted
   * when the thread limit for given board is exceeded.
   *
   * <p>The rules for updating last bump timestamp are following:
   *
   * <ol>
   *   <li>Initially it is the same as {@link #lastReplyAt}
   *   <li>When a new reply is added to this thread, it is updated to creation date of that reply
   *       (unless point 3. or 4. apply)
   *   <li>When a new reply is added to this thread and the reply is marked as {@code sage}, it is
   *       not updated.
   *   <li>When a new reply is added to this thread, but the number of replies in this thread
   *       already exceeded bump limit for this thread's board, it is not updated.
   * </ol>
   *
   * @see BoardSettings#bumpLimit
   * @see Post#sage
   */
  @Column(nullable = false)
  private OffsetDateTime lastBumpAt;

  /** {@link Board} this thread belongs to. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @Fetch(FetchMode.JOIN)
  @EqualsAndHashCode.Include
  private Board board;

  /**
   * Original (first) post of this thread. Must have attachment and non-empty body (unless the
   * {@link #subject} is set).
   */
  @OneToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @ToString.Include
  private Post originalPost;

  /**
   * Calculated statistics for this thread.
   *
   * @see ThreadStatistics
   */
  @SuppressWarnings("JpaDataSourceORMInspection")
  @OneToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id", referencedColumnName = "thread_id")
  private ThreadStatistics statistics;

  /**
   * Before inserting to database, set {@link #createdAt} and {@link #lastReplyAt} to current date
   * and time if they aren't set already.
   */
  @PrePersist
  private void setCreatedAt() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
    if (lastReplyAt == null) {
      setLastReplyAt(OffsetDateTime.now());
    }
  }

  /**
   * Retrieves thread number, which equals to {@link Post#postNumber} of {@link #originalPost}.
   *
   * @return thread number or {@code null} if {@link #originalPost} is {@code null}.
   */
  @EqualsAndHashCode.Include
  public Long getThreadNumber() {
    return originalPost != null ? originalPost.getPostNumber() : null;
  }
}
