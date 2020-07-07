package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.njuro.jard.thread.Thread;
import java.util.List;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

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
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private UUID id;

  @Column(unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String label;

  @Basic private String name;

  @Formula(
      "(SELECT CEIL(COUNT(*) / " + MAX_THREADS_PER_PAGE + ") FROM threads t WHERE t.board_id = id)")
  private int pageCount;

  @Basic @JsonIgnore private Long postCounter;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "board")
  @Builder.Default
  private BoardSettings settings = new BoardSettings();

  @Transient
  @JsonIgnoreProperties("board")
  private List<Thread> threads;
}
