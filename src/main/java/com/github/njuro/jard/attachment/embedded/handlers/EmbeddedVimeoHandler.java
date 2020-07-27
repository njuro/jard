package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import com.github.njuro.jard.attachment.Attachment;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/** Handler for embedding videos from Vimeo. */
@Component
public class EmbeddedVimeoHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "Vimeo";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://vimeo.com/api/oembed.json?autoplay=true&dnt=true");
    oembed.setUrlSchemes(Arrays.asList("https?://(?:www\\.)?vimeo\\.com/(channels/.+/)?\\d+"));
    return oembed;
  }

  @Override
  public void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    if (oembedResponse.getAuthorName() == null) {
      oembedResponse.setAuthorName("Private Video");
    }

    EmbeddedAttachmentHandler.super.setEmbedData(oembedResponse, embedUrl, attachment);
  }
}
