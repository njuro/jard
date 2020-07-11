package com.github.njuro.jard.thread;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.post.Post;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/** Entity representing a thread on board. */
@Entity
@Table(name = "threads")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Thread implements Serializable {

  private static final long serialVersionUID = -7257462911390779498L;

  /** Unique identifier of this thread. */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  @JsonIgnore
  private UUID id;

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
  @ManyToOne(targetEntity = Board.class, fetch = FetchType.EAGER, optional = false)
  @EqualsAndHashCode.Include
  private Board board;

  /** Original (first) post of this thread. */
  @OneToOne(targetEntity = Post.class, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JsonIgnoreProperties("thread")
  @ToString.Include
  private Post originalPost;

  /**
   * Calculated statistics for this thread.
   *
   * @see ThreadStatistics
   */
  @SuppressWarnings("JpaDataSourceORMInspection")
  @OneToOne(targetEntity = ThreadStatistics.class, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id", referencedColumnName = "thread_id")
  private ThreadStatistics statistics;

  /**
   * (Sub)collection of replies to this thread. Fetched by different services when needed.
   *
   * @see Post
   */
  @Transient
  @JsonIgnoreProperties("thread")
  private List<Post> replies;

  /** Before inserting to database, set creation date to current date and time. */
  @PrePersist
  private void setCreatedAt() {
    createdAt = OffsetDateTime.now();
    setLastReplyAt(OffsetDateTime.now());
  }

  /** Returns thread number, which is post number of its original post. */
  @EqualsAndHashCode.Include
  public Long getThreadNumber() {
    return originalPost != null ? originalPost.getPostNumber() : null;
  }

  public void toggleLock() {
    locked = !locked;
  }

  public void toggleSticky() {
    stickied = !stickied;
  }
}
