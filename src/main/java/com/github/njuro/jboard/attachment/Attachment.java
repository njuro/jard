package com.github.njuro.jboard.attachment;

import com.github.njuro.jboard.common.Constants;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  @Basic @EqualsAndHashCode.Include private String path;

  @NotNull private String originalFilename;

  @Column(unique = true)
  @EqualsAndHashCode.Include
  private String filename;

  @Basic private int width;

  @Basic private int height;

  @Basic private int thumbnailWidth;

  @Basic private int thumbnailHeight;

  private String awsUrl;

  private String awsThumbnailUrl;

  public File getFile() {
    return Constants.USER_CONTENT_PATH.resolve(Paths.get(path, filename)).toFile();
  }

  public File getThumbnailFile() {
    return Constants.USER_CONTENT_THUMBS_PATH.resolve(Paths.get(path, filename)).toFile();
  }

  public String getThumbnailPath() {
    return Paths.get(path, "thumbs").toString();
  }
}
