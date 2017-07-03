package com.github.njuro.models;

import javax.persistence.*;
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
    private String subject;
    @Basic
    private boolean locked;
    @Basic
    private boolean stickied;

    @Column(name = "created_date")
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    private Board board;

    @OneToMany(targetEntity = Post.class, mappedBy = "thread", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts;

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
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
