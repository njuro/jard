package com.github.njuro.jboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a thread submitted to board
 *
 * @author njuro
 */
@Entity
@Table(name = "threads")
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Basic
    private String subject;

    @Basic
    private boolean locked;

    @Basic
    private boolean stickied;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    private LocalDateTime lastReplyAt;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    private Board board;

    @OneToOne(targetEntity = Post.class, fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnoreProperties("thread")
    private Post originalPost;

    @OneToMany(targetEntity = Post.class, mappedBy = "thread", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    @ToString.Exclude
    @JsonIgnoreProperties("thread")
    private List<Post> posts;

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @EqualsAndHashCode.Include
    public Long getPostNumber() {
        return originalPost != null ? originalPost.getPostNumber() : null;
    }

    public void toggleLock() {
        this.locked = !this.locked;
    }

    public void toggleSticky() {
        this.stickied = !this.stickied;
    }
}
