package com.github.njuro.jboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.njuro.jboard.models.enums.BoardType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    @JsonIgnore
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String label;

    @Basic
    private String name;

    @Enumerated(value = EnumType.STRING)
    private BoardType type;

    @Basic
    private Long postCounter;

    public Board(String label, String name, BoardType type) {
        this.label = label;
        this.name = name;
        this.type = type;
    }
}
