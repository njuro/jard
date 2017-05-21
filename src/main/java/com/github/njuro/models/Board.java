package com.github.njuro.models;

import javax.persistence.*;

/**
 * Entity representing a board
 *
 * @author njuro
 */
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String label;
    private String name;
    private BoardType type;

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
        IMAGE, TEXT;
    }
}
