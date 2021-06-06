package com.github.njuro.jard.thread;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.post.Post;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/** Entity representing a thread on board. */
@Entity
@Table(name = "threads")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Thread extends BaseEntity {

  private static final long serialVersionUID = -7257462911390779498L;

  /** (Optional) subject of this thread. */
  @Basic private String subject;

  /** Whether this thread is locked (new replies cannot be submitted). */
  @Basic private boolean locked;

  /** Whether this thread is stickied (always on top of the board). */
  @Basic private boolean stickied;

  /** Date and time when this thread was created. */
  @Column(nullable = false)
  private OffsetDateTime createdAt;

  /** Date and time this thread was last replied to. */
  @Column(nullable = false)
  private OffsetDateTime lastReplyAt;

  /** Date and time this thread was last bumped. */
  @Column(nullable = false)
  private OffsetDateTime lastBumpAt;

  /** {@link Board} this board belongs to. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @Fetch(FetchMode.JOIN)
  @EqualsAndHashCode.Include
  private Board board;

  /** Original (first) post of this thread. */
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

  /** Before inserting to database, set creation date to current date and time. */
  @PrePersist
  private void setCreatedAt() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
    if (lastReplyAt == null) {
      setLastReplyAt(OffsetDateTime.now());
    }
  }

  /** Returns thread number, which is post number of its original post. */
  @EqualsAndHashCode.Include
  public Long getThreadNumber() {
    return originalPost != null ? originalPost.getPostNumber() : null;
  }
}
