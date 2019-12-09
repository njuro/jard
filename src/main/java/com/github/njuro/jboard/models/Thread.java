package com.github.njuro.jboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

  @Column(name = "createdAt")
  private LocalDateTime createdAt;

  private LocalDateTime lastReplyAt;

  @ManyToOne(
      targetEntity = Board.class,
      fetch = FetchType.LAZY,
      optional = false,
      cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @EqualsAndHashCode.Include
  private Board board;

  @OneToOne(targetEntity = Post.class, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @NotNull
  @JsonIgnoreProperties("thread")
  private Post originalPost;

  @OneToOne(targetEntity = ThreadStatistics.class, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id", referencedColumnName = "thread_id")
  private ThreadStatistics statistics;

  @Transient
  @JsonIgnoreProperties("thread")
  private List<Post> replies;

  @PrePersist
  private void setCreatedAt() {
    this.createdAt = LocalDateTime.now();
    this.setLastReplyAt(LocalDateTime.now());
  }

  @EqualsAndHashCode.Include
  public Long getPostNumber() {
    return this.originalPost != null ? this.originalPost.getPostNumber() : null;
  }

  public void toggleLock() {
    this.locked = !this.locked;
  }

  public void toggleSticky() {
    this.stickied = !this.stickied;
  }
}
