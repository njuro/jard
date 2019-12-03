package com.github.njuro.jboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jboard.models.enums.BoardAttachmentType;
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
@AllArgsConstructor
@NoArgsConstructor
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
    private BoardAttachmentType attachmentType;

    private boolean nsfw;

    @Basic
    private Long postCounter;

    @OneToMany(targetEntity = Thread.class, fetch = FetchType.LAZY, mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("stickied DESC, createdAt DESC")
    @ToString.Exclude
    @JsonIgnoreProperties("board")
    private List<Thread> threads;

}
