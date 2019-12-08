package com.github.njuro.jboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jboard.models.dto.ThreadCatalogEntry;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@SqlResultSetMapping(
    name = "ThreadCatalogEntry",
    classes =
        @ConstructorResult(
            targetClass = ThreadCatalogEntry.class,
            columns = {
              @ColumnResult(name = "subject"),
              @ColumnResult(name = "originalPostBody"),
              @ColumnResult(name = "stickied"),
              @ColumnResult(name = "locked"),
              @ColumnResult(name = "replyCount", type = Integer.class),
              @ColumnResult(name = "attachmentCount", type = Integer.class)
            }))
@NamedNativeQuery(
    name = "Thread.getThreadCatalogEntries",
    resultSetMapping = "ThreadCatalogEntry",
    query =
        "SELECT t.subject AS subject,\n"
            + "       op.body AS originalPostBody,\n"
            + "       t.stickied AS stickied,\n"
            + "       t.locked AS locked,\n"
            + "       COUNT(p.id) AS replyCount,\n"
            + "       COUNT(a.id) AS attachmentCount\n"
            + "FROM threads t\n"
            + "    LEFT JOIN posts op ON t.original_post_id = op.id\n"
            + "    LEFT JOIN posts p ON t.id = p.thread_id AND t.original_post_id != p.id\n"
            + "    LEFT JOIN attachments a on p.attachment_id = a.id\n"
            + "WHERE t.board_id = :boardId\n"
            + "GROUP BY t.id")
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

  @ManyToOne(targetEntity = Board.class, fetch = FetchType.EAGER, optional = false)
  @EqualsAndHashCode.Include
  private Board board;

  @OneToOne(targetEntity = Post.class, fetch = FetchType.EAGER)
  @NotNull
  @JsonIgnoreProperties("thread")
  private Post originalPost;

  @OneToMany(targetEntity = Post.class, mappedBy = "thread", fetch = FetchType.LAZY)
  @OrderBy("createdAt ASC")
  @ToString.Exclude
  @JsonIgnoreProperties("thread")
  private List<Post> posts;

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
