package com.github.njuro.models;

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
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private Board board;

    @OneToMany(targetEntity = Post.class, mappedBy = "thread", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    private Post originalPost;

    public Thread() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isStickied() {
        return stickied;
    }

    public void setStickied(boolean stickied) {
        this.stickied = stickied;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Post getOriginalPost() {
        return originalPost;
    }

    public void setOriginalPost(Post originalPost) {
        this.originalPost = originalPost;
    }


    @Override
    public String toString() {
        return "Thread{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", locked=" + locked +
                ", stickied=" + stickied +
                ", board=" + board +
                '}';
    }
}
