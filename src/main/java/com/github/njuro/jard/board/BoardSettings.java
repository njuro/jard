package com.github.njuro.jard.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategorySerializer;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/** Entity representing editable settings of a {@link Board}. */
@Entity
@Table(name = "board_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSettings implements Serializable {

  private static final long serialVersionUID = 7024830970057024626L;

  /** Identifier of these settings. Equals to primary key of owning {@link #board}. */
  @Id @JsonIgnore private UUID boardId;

  /** {@link Board} these settings belong to. */
  @OneToOne
  @JoinColumn(name = "board_id")
  @MapsId
  @JsonIgnore
  private Board board;

  /**
   * Enabled attachment categories for this board.
   *
   * @see AttachmentCategory
   */
  @SuppressWarnings("JpaDataSourceORMInspection")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "board_attachment_categories",
      joinColumns = @JoinColumn(name = "board_id"))
  @Column(name = "attachment_category")
  @Enumerated(value = EnumType.STRING)
  @Builder.Default
  @JsonSerialize(contentUsing = AttachmentCategorySerializer.class)
  private Set<AttachmentCategory> attachmentCategories = new HashSet<>();

  /**
   * How many threads can be active on this board at once. If surpassed, the most stale (= longest
   * time since bump) thread will be deleted.
   */
  @ColumnDefault("100")
  private int threadLimit;

  /**
   * How many posts in thread before new replies stop bumping it (causing it to become more stale
   * and eventually getting deleted).
   */
  @ColumnDefault("300")
  private int bumpLimit;

  /** If true, adult (Not Safe For Work) content is allowed on this board. */
  @Basic private boolean nsfw;

  /** Predefined poster name for new posts. */
  @Basic private String defaultPosterName;

  /** If true, all new posts will have poster name set to {@link #defaultPosterName}. */
  @Basic private boolean forceDefaultPosterName;

  /** If true, post will have flags indicating country of the poster (based on IP address). */
  @Basic private boolean countryFlags;
}
