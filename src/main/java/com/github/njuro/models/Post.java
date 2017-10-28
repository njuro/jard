package com.github.njuro.models;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a post in thread
 *
 * @author njuro
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private Long postNumber;

    @Basic
    private String name;

    @Basic
    private String tripcode;

    @Basic
    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @ManyToOne(targetEntity = Thread.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Thread thread;

    public Post() {
    }

    public Post(String name, String tripcode, String body) {
        this.name = name;
        this.tripcode = tripcode;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(Long postNumber) {
        this.postNumber = postNumber;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        Post post = (Post) o;

        if (!getPostNumber().equals(post.getPostNumber())) return false;
        return getThread() != null ? getThread().equals(post.getThread()) : post.getThread() == null;
    }

    @Override
    public int hashCode() {
        int result = getPostNumber().hashCode();
        result = 31 * result + (getThread() != null ? getThread().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postNumber=" + postNumber +
                ", name='" + name + '\'' +
                ", body='" + body + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
