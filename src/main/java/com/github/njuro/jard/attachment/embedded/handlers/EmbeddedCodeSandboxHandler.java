package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import com.github.njuro.jard.attachment.Attachment;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/** Handler for embedding code from CodeSandbox. */
@Component
public class EmbeddedCodeSandboxHandler implements EmbeddedAttachmentHandler {
  @Override
  public String getProviderName() {
    return "CodeSandbox";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://codesandbox.io/oembed");
    oembed.setUrlSchemes(
        Arrays.asList("https?://codesandbox\\.io/s/.*", "https?://codesandbox\\.io/embed/.*"));
    return oembed;
  }

  @Override
  public void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    if (oembedResponse.getAuthorName() == null) {
      oembedResponse.setAuthorName("Unknown");
    }

    EmbeddedAttachmentHandler.super.setEmbedData(oembedResponse, embedUrl, attachment);
  }
}
