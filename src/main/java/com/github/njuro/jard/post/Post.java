package com.github.njuro.jard.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.thread.Thread;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Entity representing a post in thread
 *
 * @author njuro
 */
@Entity
@Table(name = "posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private UUID id;

  @Basic @EqualsAndHashCode.Include private Long postNumber;

  @Basic private String name;

  @Basic private String tripcode;

  @Basic
  @Column(columnDefinition = "TEXT")
  private String body;

  private LocalDateTime createdAt;

  @Basic private String ip;

  @ManyToOne(targetEntity = Thread.class, fetch = FetchType.EAGER)
  @EqualsAndHashCode.Include
  @ToString.Exclude
  private Thread thread;

  @OneToOne(targetEntity = Attachment.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @Fetch(FetchMode.JOIN)
  @ToString.Exclude
  private Attachment attachment;

  @PrePersist
  private void setCreatedAt() {
    createdAt = LocalDateTime.now();
  }
}
