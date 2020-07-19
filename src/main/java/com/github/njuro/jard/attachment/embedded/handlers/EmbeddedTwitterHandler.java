package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import ac.simons.oembed.OembedResponse.Format;
import com.github.njuro.jard.attachment.Attachment;
import java.util.Arrays;
import org.jsoup.Jsoup;
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
  public String getTitle(OembedResponse response) {
    return "Tweet by " + response.getAuthorName();
  }

  @Override
  public void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    oembedResponse.setThumbnailUrl("https://i.imgur.com/4QGqHUC.png");
    var document = Jsoup.parseBodyFragment(oembedResponse.getHtml());
    document
        .getElementsByClass("twitter-tweet")
        .forEach(element -> element.attr("data-dnt", "true"));
    oembedResponse.setHtml(document.body().html());

    EmbeddedAttachmentHandler.super.setEmbedData(oembedResponse, embedUrl, attachment);
  }
}
