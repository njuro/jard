package com.github.njuro.jboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
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

    @Basic
    private String ip;

    @ManyToOne(targetEntity = Thread.class, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    @ToString.Exclude
    private Thread thread;

    @OneToOne(targetEntity = Attachment.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Attachment attachment;

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
