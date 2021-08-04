package com.github.njuro.jard.attachment.embedded.handlers;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import ac.simons.oembed.OembedResponse.Format;
import com.github.njuro.jard.attachment.Attachment;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** Handler for embedding videos from YouTube. */
@Component
public class EmbeddedYoutubeHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "YouTube";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(Format.json);
    oembed.setEndpoint("https://youtube.com/oembed");
    oembed.setUrlSchemes(
        Arrays.asList(
            "https?://(?:[^.]+\\.)?youtube\\.com/watch/?\\?(?:.+&)?v=([^&]+)",
            "https?://(?:[^.]+\\.)?(?:youtu\\.be|youtube\\.com/embed)/([a-zA-Z0-9_-]+)"));
    return oembed;
  }

  @Override
  public void setEmbedData(OembedResponse oembedResponse, String embedUrl, Attachment attachment) {
    // use privacy respecting domain for embedding
    oembedResponse.setHtml(
        oembedResponse.getHtml().replaceAll("youtu\\.be|youtube\\.com", "youtube-nocookie.com"));

    var iframe = Jsoup.parseBodyFragment(oembedResponse.getHtml()).body().child(0);
    iframe.attr(
        "src",
        UriComponentsBuilder.fromHttpUrl(iframe.attr("src"))
            .queryParam("autoplay", 1)
            .toUriString());
    oembedResponse.setHtml(iframe.outerHtml());

    EmbeddedAttachmentHandler.super.setEmbedData(oembedResponse, embedUrl, attachment);
  }
}
