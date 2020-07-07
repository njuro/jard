package com.github.njuro.jard.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategorySerializer;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "board_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSettings implements Serializable {

  private static final long serialVersionUID = 7024830970057024626L;

  @Id @JsonIgnore private UUID boardId;

  @OneToOne
  @JoinColumn(name = "board_id")
  @MapsId
  @JsonIgnore
  private Board board;

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

  @ColumnDefault("100")
  private int threadLimit;

  @ColumnDefault("300")
  private int bumpLimit;

  @Basic private boolean nsfw;

  @Basic private String defaultPosterName;

  @Basic private boolean forceDefaultPosterName;
}
