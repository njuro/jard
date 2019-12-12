package com.github.njuro.jboard.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jboard.thread.Thread;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

  @Column(unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String label;

  @Basic private String name;

  @Enumerated(value = EnumType.STRING)
  private BoardAttachmentType attachmentType;

  private boolean nsfw;

  @ColumnDefault("100")
  private int threadLimit;

  @ColumnDefault("300")
  private int bumpLimit;

  @Transient
  @JsonIgnoreProperties("board")
  private List<Thread> threads;

  @Basic @JsonIgnore private Long postCounter;
}
