package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import com.github.njuro.jard.attachment.Attachment;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/** Handler for embedding code from CodePen. */
@Component
public class EmbeddedCodePenHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "CodePen";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://codepen.io/api/oembed");
    oembed.setUrlSchemes(Arrays.asList("https?://codepen\\.io/.+/pen/.+"));
    return oembed;
  }

  @Override
  public void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    oembedResponse.setHtml(oembedResponse.getHtml().replace("/embed/preview/", "/embed/"));
    EmbeddedAttachmentHandler.super.setEmbedData(oembedResponse, embedUrl, attachment);
  }
}
