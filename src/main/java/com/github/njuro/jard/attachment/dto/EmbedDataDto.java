package com.github.njuro.jard.attachment.dto;

import com.github.njuro.jard.attachment.EmbedData;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for {@link EmbedData}. */
@SuppressWarnings("JavadocReference")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbedDataDto implements Serializable {
  @Serial private static final long serialVersionUID = 5556727553556549679L;

  /** {@link EmbedData#embedUrl} */
  private String embedUrl;

  /** {@link EmbedData#thumbnailUrl} */
  private String thumbnailUrl;

  /** {@link EmbedData#providerName} */
  private String providerName;

  /** {@link EmbedData#uploaderName} */
  private String uploaderName;

  /** {@link EmbedData#renderedHtml} */
  private String renderedHtml;
}
