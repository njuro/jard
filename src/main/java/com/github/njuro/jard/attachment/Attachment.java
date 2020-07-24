package com.github.njuro.jard.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategorySerializer;
import com.github.njuro.jard.attachment.embedded.EmbedData;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.post.Post;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;

/** Entity representing an attachment to {@link Post}. */
@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Attachment implements Serializable {

  private static final long serialVersionUID = -751675348099883626L;

  /** Unique identifier of this attachment. */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  @JsonIgnore
  private UUID id;

  /**
   * Category this attachment belongs to.
   *
   * @see AttachmentCategory
   */
  @Enumerated(EnumType.STRING)
  @JsonSerialize(using = AttachmentCategorySerializer.class)
  @ToString.Include
  private AttachmentCategory category;

  /** Parent folder(s) this attachment is stored in. Example: {@code /foo/pol/}. */
  @Basic @EqualsAndHashCode.Include @ToString.Include private String folder;

  /**
   * Filename given to the attachment's file by the poster. For embedded attachments it's the title
   * of embedded content.
   */
  @Basic
  @Column(nullable = false)
  @ToString.Include
  private String originalFilename;

  /**
   * Filename generated by the system. The file will be stored under this name. Embedded attachments
   * are not stored locally, so it will be {@code null} for them.
   */
  @Column(unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String filename;

  /** (Optional) name of stored thumbnail for this attachment's file. */
  @Column(unique = true)
  @EqualsAndHashCode.Include
  private String thumbnailFilename;

  /** (Optional) shareable url to this attachment's file on remote storage server. */
  private String remoteStorageUrl;

  /** (Optional) shareable url to thumbnail for this attachment's file on remote storage server. */
  private String remoteStorageThumbnailUrl;

  /**
   * (Optional) metadata for this attachment.
   *
   * @see AttachmentMetadata
   */
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "attachment")
  private AttachmentMetadata metadata;

  /**
   * (Optional) data for embedded attachment.
   *
   * @see EmbedData
   */
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "attachment")
  private EmbedData embedData;

  /**
   * @return pointer to this attachment's file on local filesystem. If file does not exist (embedded
   *     attachments) return {@code null}
   */
  public File getFile() {
    if (filename == null) {
      return null;
    }

    return Constants.USER_CONTENT_PATH.resolve(Paths.get(folder, filename)).toFile();
  }

  /**
   * @return pointer to thumbnail for this attachment's file on local filesystem. If thumbnail does
   *     not exist, returns {@code null}.
   */
  public File getThumbnailFile() {
    if (thumbnailFilename == null) {
      return null;
    }

    return Constants.USER_CONTENT_PATH
        .resolve(Paths.get(getThumbnailFolder(), thumbnailFilename))
        .toFile();
  }

  /**
   * @return parent folder(s) thumbnail for this attachment's file is stored in. If thumbnail does
   *     not exist, returns {@code null}.
   */
  @JsonProperty("thumbnailFolder")
  public String getThumbnailFolder() {
    if (thumbnailFilename == null) {
      return null;
    }

    return Paths.get(folder, "thumbs").toString();
  }
}
