package com.github.njuro.jboard.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
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
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    private String subject;

    @Basic
    private boolean locked;

    @Basic
    private boolean stickied;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    private Board board;

    @OneToMany(targetEntity = Post.class, mappedBy = "thread", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    @ToString.Exclude
    private List<Post> posts;

    @OneToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Include
    private Post originalPost;

    public Thread(String subject, Board board) {
        this.subject = subject;
        this.board = board;
    }

    public Thread(String subject, boolean locked, boolean stickied, Board board) {
        this.subject = subject;
        this.locked = locked;
        this.stickied = stickied;
        this.board = board;
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

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
