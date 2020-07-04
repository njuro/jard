package com.github.njuro.jboard.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachments_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentMetadata implements Serializable {

  private static final long serialVersionUID = -8455977079986712310L;

  @Id @JsonIgnore private UUID attachmentId;

  @OneToOne
  @JoinColumn(name = "attachment_id")
  @MapsId
  @JsonIgnore
  private Attachment attachment;

  @Basic private String mimeType;

  @Basic private int width;

  @Basic private int height;

  @Basic private int thumbnailWidth;

  @Basic private int thumbnailHeight;

  @Basic private String fileSize;

  @Basic private String duration;

  @Basic private String hash;
}
