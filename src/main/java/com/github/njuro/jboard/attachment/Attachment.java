package com.github.njuro.jboard.attachment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jboard.attachment.AttachmentType.AttachmentTypeSerializer;
import com.github.njuro.jboard.common.Constants;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing an attachment to post
 *
 * @author njuro
 */
@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Attachment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @JsonSerialize(using = AttachmentTypeSerializer.class)
  private AttachmentType type;

  @Basic @EqualsAndHashCode.Include private String folder;

  @NotNull private String originalFilename;

  @Column(unique = true)
  @EqualsAndHashCode.Include
  private String filename;

  @Column(unique = true)
  @EqualsAndHashCode.Include
  private String thumbnailFilename;

  private String awsUrl;

  private String awsThumbnailUrl;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "attachment")
  @Builder.Default
  private AttachmentMetadata metadata = new AttachmentMetadata();

  public File getFile() {
    return Constants.USER_CONTENT_PATH.resolve(Paths.get(folder, filename)).toFile();
  }

  public File getThumbnailFile() {
    if (thumbnailFilename == null) {
      return null;
    }

    return Constants.USER_CONTENT_THUMBS_PATH
        .resolve(Paths.get(folder, thumbnailFilename))
        .toFile();
  }

  public String getThumbnailFolder() {
    if (thumbnailFilename == null) {
      return null;
    }

    return Paths.get(folder, "thumbs").toString();
  }
}
