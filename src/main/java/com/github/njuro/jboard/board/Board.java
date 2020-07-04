package com.github.njuro.jboard.board;

import static com.github.njuro.jboard.common.Constants.MAX_THREADS_PER_PAGE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jboard.attachment.AttachmentType;
import com.github.njuro.jboard.attachment.AttachmentType.AttachmentTypeSerializer;
import com.github.njuro.jboard.thread.Thread;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
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

  @Enumerated(value = EnumType.STRING)
  private BoardAttachmentType attachmentType;

  private boolean nsfw;

  @Formula(
      "(SELECT CEIL(COUNT(*) / " + MAX_THREADS_PER_PAGE + ") FROM threads t WHERE t.board_id = id)")
  private int pageCount;

  @ColumnDefault("100")
  private int threadLimit;

  @ColumnDefault("300")
  private int bumpLimit;

  @Transient
  @JsonIgnoreProperties("board")
  private List<Thread> threads;

  @Basic @JsonIgnore private Long postCounter;
}
