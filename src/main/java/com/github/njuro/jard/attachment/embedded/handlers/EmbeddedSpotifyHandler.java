package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import com.github.njuro.jard.attachment.Attachment;
import java.util.Collections;
import org.springframework.stereotype.Component;

/** Handler for embedding songs from Spotify. */
@Component
public class EmbeddedSpotifyHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "Spotify";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://open.spotify.com/oembed");
    oembed.setUrlSchemes(Collections.singletonList("https?://(open|play).spotify.com/.+/.+"));
    return oembed;
  }

  @Override
  public void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    oembedResponse.setAuthorName("Unknown Artist");

    EmbeddedAttachmentHandler.super.setEmbedData(oembedResponse, embedUrl, attachment);
  }
}
