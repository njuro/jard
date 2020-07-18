package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse.Format;
import com.github.njuro.jard.attachment.AttachmentCategory;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/** Handler for embedding tweets from Twitter. */
@Component
public class EmbeddedTwitterHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "Twitter";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(Format.json);
    oembed.setEndpoint("https://publish.twitter.com/oembed");
    oembed.setUrlSchemes(
        Arrays.asList(
            "https?://(?:www|mobile\\.)?twitter\\.com/(?:#!/)?([^/]+)/status(?:es)?/(\\d+)"));
    return oembed;
  }

  @Override
  public AttachmentCategory getAttachmentCategory() {
    return AttachmentCategory.TEXT;
  }
}
