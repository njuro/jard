package com.github.njuro.jard.attachment;

import com.github.njuro.jard.attachment.embedded.handlers.EmbeddedAttachmentHandler;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing data about embedded {@link Attachment} */
@Entity
@Table(name = "attachments_embed_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbedData implements Serializable {

  @Serial private static final long serialVersionUID = -6639106432940190340L;

  /** Identifier of these embed data. Equals to primary key of owning {@link #attachment}. */
  @Id private UUID attachmentId;

  /** {@link Attachment} these embed data belong to. */
  @OneToOne
  @JoinColumn(name = "attachment_id")
  @MapsId
  private Attachment attachment;

  /** URL to content to be embedded. */
  @Basic
  @Column(nullable = false)
  private String embedUrl;

  /** (Optional) URL to thumbnail for this content on provider's server. */
  @Basic private String thumbnailUrl;

  /** {@link EmbeddedAttachmentHandler#getProviderName()} */
  @Basic
  @Column(nullable = false)
  private String providerName;

  /** Name of original uploader of this embedded content. */
  @Basic
  @Column(nullable = false)
  private String uploaderName;

  /** Rendered HTML for embedding this attachment. */
  @Basic
  @Column(columnDefinition = "TEXT", nullable = false)
  private String renderedHtml;
}
