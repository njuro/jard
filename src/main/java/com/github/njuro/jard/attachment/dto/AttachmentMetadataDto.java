package com.github.njuro.jard.attachment.dto;

import com.github.njuro.jard.attachment.AttachmentMetadata;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for {@link AttachmentMetadata}. */
@SuppressWarnings("JavadocReference")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentMetadataDto implements Serializable {
  private static final long serialVersionUID = -85086973234896633L;

  /** {@link AttachmentMetadata#mimeType } */
  private String mimeType;

  /** {@link AttachmentMetadata#width } */
  private int width;

  /** {@link AttachmentMetadata#height } */
  private int height;

  /** {@link AttachmentMetadata#thumbnailWidth } */
  private int thumbnailWidth;

  /** {@link AttachmentMetadata#thumbnailHeight } */
  private int thumbnailHeight;

  /** {@link AttachmentMetadata#fileSize } */
  private String fileSize;

  /** {@link AttachmentMetadata#duration } */
  private String duration;

  /** {@link AttachmentMetadata#checksum } */
  private String checksum;
}
