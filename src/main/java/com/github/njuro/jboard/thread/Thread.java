package com.github.njuro.jboard.thread;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.post.Post;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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

/**
 * Entity representing a thread submitted to board
 *
 * @author njuro
 */
@Entity
@Table(name = "threads")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Thread {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Basic private String subject;

  @Basic private boolean locked;

  @Basic private boolean stickied;

  private LocalDateTime createdAt;

  private LocalDateTime lastReplyAt;

  @ManyToOne(targetEntity = Board.class, fetch = FetchType.EAGER, optional = false)
  @EqualsAndHashCode.Include
  @ToString.Exclude
  private Board board;

  @OneToOne(targetEntity = Post.class, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JsonIgnoreProperties("thread")
  private Post originalPost;

  @SuppressWarnings("JpaDataSourceORMInspection")
  @OneToOne(targetEntity = ThreadStatistics.class, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id", referencedColumnName = "thread_id")
  private ThreadStatistics statistics;

  @Transient
  @JsonIgnoreProperties("thread")
  private List<Post> replies;

  @PrePersist
  private void setCreatedAt() {
    createdAt = LocalDateTime.now();
    setLastReplyAt(LocalDateTime.now());
  }

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
