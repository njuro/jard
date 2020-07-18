package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.embedded.EmbedData;
import com.github.njuro.jard.attachment.embedded.EmbedService;

/**
 * Interface for registering custom providers of embedded content using OEmbed format.
 *
 * @see EmbedService
 */
public interface EmbeddedAttachmentHandler {

  /** @return name of the content provider. Cannot be {@code null}. */
  String getProviderName();

  /**
   * @return OEmbed endpoint of the content provider
   * @see OembedEndpoint
   */
  OembedEndpoint registerEndpoint();

  /** @return general category of content from this provider (used for creating thumbnails) */
  AttachmentCategory getAttachmentCategory();

  /**
   * Sets embed data on given attachment.
   *
   * @param oembedResponse Response from content provider's OEmbed endpoint
   * @param embedUrl URL to content to be embedded
   * @param attachment Post attachment to set embed data to
   * @see OembedResponse
   * @see EmbedData
   */
  default void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    attachment.setOriginalFilename(getProviderName());

    var embedData =
        EmbedData.builder()
            .category(getAttachmentCategory())
            .providerName(getProviderName())
            .embedUrl(embedUrl)
            .uploaderName(oembedResponse.getAuthorName())
            .renderedHtml(oembedResponse.getHtml())
            .build();

    if (oembedResponse.getThumbnailUrl() != null) {
      embedData.setThumbnailUrl(oembedResponse.getThumbnailUrl());
    }

    attachment.setEmbedData(embedData);
  }
}
