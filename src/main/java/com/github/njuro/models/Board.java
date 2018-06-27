package com.github.njuro.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Entity representing a board
 *
 * @author njuro
 */
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    @Size(min = 1, max = 16)
    private String label;

    @Basic
    private String name;

    @Enumerated(value = EnumType.STRING)
    private BoardType type;

    @OneToMany(targetEntity = Thread.class, fetch = FetchType.LAZY, mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Thread> threads;

    @Basic
    private Long postCounter;

    public Board() {

    }

    public Board(String label, String name, BoardType type) {
        this.label = label;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BoardType getType() {
        return type;
    }

    public void setType(BoardType type) {
        this.type = type;
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public void setThreads(List<Thread> threads) {
        this.threads = threads;
    }

    public Long getPostCounter() {
        return postCounter;
    }

    public void setPostCounter(Long postCounter) {
        this.postCounter = postCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;

        Board board = (Board) o;

        return getLabel() != null ? getLabel().equals(board.getLabel()) : board.getLabel() == null;
    }

    @Override
    public int hashCode() {
        return getLabel() != null ? getLabel().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public enum BoardType {
        IMAGE, TEXT
    }
}
