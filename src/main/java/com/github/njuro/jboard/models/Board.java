package com.github.njuro.jboard.models;

import com.github.njuro.jboard.models.enums.BoardType;
import lombok.*;

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
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
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
}
