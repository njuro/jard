package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.DefaultRequestProvider;
import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import java.net.URI;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Component;

/** Handler for embedding Reddit content. */
@Component
public class EmbeddedRedditHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "Reddit";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(OembedResponse.Format.json);
    oembed.setEndpoint("https://reddit.com/oembed?parent=true&live=false");
    oembed.setUrlSchemes(List.of("https?://(old\\.)?reddit\\.com/r/.*/comments/.*/.*"));
    oembed.setRequestProviderClass(RedditRequestProvider.class);
    return oembed;
  }

  /** Reddit doesn't like bot User-Agent so we spoof UA to look like regular browser. */
  public static class RedditRequestProvider extends DefaultRequestProvider {
    @Override
    public HttpGet createRequestFor(String userAgent, String applicationName, URI uri) {
      return super.createRequestFor(
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36",
          applicationName,
          uri);
    }
  }
}
