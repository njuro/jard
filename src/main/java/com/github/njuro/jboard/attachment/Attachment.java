package com.github.njuro.jboard.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.njuro.jboard.common.Constants;
import java.io.File;
import java.nio.file.Paths;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

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
public class Attachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Basic @EqualsAndHashCode.Include private String path;

  @NotNull private String originalFilename;

  @Column(unique = true)
  @EqualsAndHashCode.Include
  private String filename;

  @Basic private int width;

  @Basic private int height;

  @Basic private int thumbWidth;

  @Basic private int thumbHeight;

  @Transient @ToString.Exclude private String url;

  @Transient @ToString.Exclude @JsonIgnore private File file;

  @Transient @ToString.Exclude @JsonIgnore private MultipartFile sourceFile;

  public Attachment(String path, @NotNull String originalFilename, String filename) {
    this.path = path;
    this.originalFilename = originalFilename;
    this.filename = filename;

    initContentPaths();
  }

  @PostLoad
  public void initContentPaths() {
    url = Constants.USER_CONTENT_URL + Paths.get(path, filename).toString();
    file = Constants.USER_CONTENT_PATH.resolve(Paths.get(path, filename)).toFile();
  }
}
