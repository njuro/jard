package com.github.njuro.models;

import javax.persistence.*;

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

    private String subject;
    private boolean locked;
    private boolean stickied;

    @ManyToOne
    private Board board;

    public Thread() {

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
