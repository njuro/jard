package com.github.njuro.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Entity representing a board
 *
 * @author njuro
 */
@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String label;

    @Basic
    private String name;

    @Enumerated(value = EnumType.STRING)
    private BoardType type;

    @OneToMany(targetEntity = Thread.class, fetch = FetchType.LAZY, mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    @ToString.Exclude
    private List<Thread> threads;

    @Basic
    private Long postCounter;

    public Board(String label, String name, BoardType type) {
        this.label = label;
        this.name = name;
        this.type = type;
    }

    public enum BoardType {
        IMAGE, TEXT
    }
}
