package com.github.njuro.jard.attachment;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing metadata of {@link Attachment}. */
@Entity
@Table(name = "attachments_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentMetadata implements Serializable {

  @Serial private static final long serialVersionUID = -8455977079986712310L;

  /** Identifier of these metadata. Equals to primary key of owning {@link #attachment}. */
  @Id private UUID attachmentId;

  /** {@link Attachment} these metadata belong to. */
  @OneToOne
  @JoinColumn(name = "attachment_id")
  @MapsId
  private Attachment attachment;

  /** Standard MIME type of this attachment's file, e.g. {@code image/jpeg}. */
  @Basic
  @Column(nullable = false)
  private String mimeType;

  /** (Optional) width (in pixels) of this attachment's file. */
  @Basic private int width;

  /** (Optional) height (in pixels) of this attachment's file. */
  @Basic private int height;

  /** (Optional) width (in pixels) of thumbnail for this attachment's file. */
  @Basic private int thumbnailWidth;

  /** (Optional) height (in pixels) of thumbnail for this attachment's file. */
  @Basic private int thumbnailHeight;

  /** Formatted size of this attachment's file, e.g. {@code 2.5MB}. */
  @Basic
  @Column(nullable = false)
  private String fileSize;

  /** (Optional) formatted duration of this attachment's file, e.g. {@code 00:02:30}. */
  @Basic private String duration;

  /**
   * Hash of content bytes of this attachment's files e.g. {@code 9e107d9d372bb6826bd81d3542a419d6}
   */
  @Basic
  @Column(nullable = false)
  private String checksum;
}
