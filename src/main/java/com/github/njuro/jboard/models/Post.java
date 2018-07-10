package com.github.njuro.jboard.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a post in thread
 *
 * @author njuro
 */
@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @EqualsAndHashCode.Include
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
    @EqualsAndHashCode.Include
    @ToString.Exclude
    private Thread thread;

    @OneToOne(targetEntity = Attachment.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Attachment attachment;

    public Post(String name, String tripcode, String body) {
        this.name = name;
        this.tripcode = tripcode;
        this.body = body;
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
