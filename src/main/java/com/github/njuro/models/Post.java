package com.github.njuro.models;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by juro on 6/17/17.
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private String name;

    @Basic
    private String tripcode;

    @Basic
    private String body;

    @Column(name = "created_date")
    private LocalDateTime dateTime;

    @ManyToOne(targetEntity = Thread.class, fetch = FetchType.LAZY)
    private Thread thread;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTripcode() {
        return tripcode;
    }

    public void setTripcode(String tripcode) {
        this.tripcode = tripcode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
