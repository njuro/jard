package com.github.njuro.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a thread
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
    @Size(max = 255)
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
}
