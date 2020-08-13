package com.github.njuro.jard.attachment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategorySerializer;
import com.github.njuro.jard.base.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** DTO for {@link Attachment}. */
@SuppressWarnings("JavadocReference")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class AttachmentDto extends BaseDto {
  private static final long serialVersionUID = -9156850050978422063L;

  /** {@link Attachment#category */
  @JsonSerialize(using = AttachmentCategorySerializer.class)
  @ToString.Include
  private AttachmentCategory category;

  /** {@link Attachment#folder */
  @EqualsAndHashCode.Include @ToString.Include private String folder;

  /** {@link Attachment#originalFilename */
  @ToString.Include private String originalFilename;

  /** {@link Attachment#filename */
  @EqualsAndHashCode.Include @ToString.Include private String filename;

  /** {@link Attachment#thumbnailFilename */
  private String thumbnailFilename;

  /** {@link Attachment#getThumbnailFolder()} */
  private String thumbnailFolder;

  /** {@link Attachment#remoteStorageUrl */
  private String remoteStorageUrl;

  /** {@link Attachment#remoteStorageThumbnailUrl */
  private String remoteStorageThumbnailUrl;

  /** {@link Attachment#metadata */
  private AttachmentMetadataDto metadata;

  /** {@link Attachment#embedData */
  private EmbedDataDto embedData;
}
