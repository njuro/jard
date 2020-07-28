package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import java.util.Collections;
import org.springframework.stereotype.Component;

/** Handler for embedding videos from TikTok. */
@Component
public class EmbeddedTikTokHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "TikTok";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://www.tiktok.com/oembed");
    oembed.setUrlSchemes(Collections.singletonList("https?://(www.)?tiktok.com/.+/video/.+"));
    return oembed;
  }
}
