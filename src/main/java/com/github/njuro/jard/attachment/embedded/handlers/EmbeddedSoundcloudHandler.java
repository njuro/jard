package com.github.njuro.jard.attachment.embedded.handlers;

import static com.github.njuro.jard.common.Constants.IMAGE_MAX_THUMB_WIDTH;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse.Format;
import com.github.njuro.jard.attachment.AttachmentCategory;
import java.util.Collections;
import org.springframework.stereotype.Component;

/** Handler for embedding tracks from SoundCloud. */
@Component
public class EmbeddedSoundcloudHandler implements EmbeddedAttachmentHandler {

  @Override
  public String getProviderName() {
    return "SoundCloud";
  }

  @Override
  public OembedEndpoint registerEndpoint() {
    var oembed = new OembedEndpoint();
    oembed.setName(getProviderName());
    oembed.setFormat(Format.json);
    oembed.setEndpoint("https://soundcloud.com/oembed");
    // Default width for SC is "100%" which causes JSON parsing exception
    oembed.setMaxWidth((int) IMAGE_MAX_THUMB_WIDTH);
    oembed.setUrlSchemes(Collections.singletonList("https?://soundcloud.com/.*/.*"));
    return oembed;
  }

  @Override
  public AttachmentCategory getAttachmentCategory() {
    return AttachmentCategory.AUDIO;
  }
}
