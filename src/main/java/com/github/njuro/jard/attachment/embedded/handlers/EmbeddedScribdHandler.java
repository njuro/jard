package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/** Handler for embedding documents from Scribd. */
@Component
public class EmbeddedScribdHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "Scribd";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://www.scribd.com/services/oembed");
    oembed.setUrlSchemes(Arrays.asList("https?://(www\\.)?scribd.com(/mobile/)?/doc(ument)?s?/.+"));
    return oembed;
  }
}
