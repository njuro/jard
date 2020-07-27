package com.github.njuro.jard.attachment;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jard.attachment.embedded.EmbedService;
import com.github.njuro.jard.attachment.embedded.handlers.EmbeddedAttachmentHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class EmbedAttachmentTest {

  @Configuration
  @ComponentScan(
      includeFilters =
          @ComponentScan.Filter(
              type = FilterType.ASSIGNABLE_TYPE,
              value = EmbeddedAttachmentHandler.class),
      useDefaultFilters = false)
  @Import(EmbedService.class)
  static class EmbedConfiguration {}

  @Autowired private EmbedService embedService;
  private Attachment attachment;

  @Test
  void testCodePen() {
    testEmbed("https://codepen.io/lynnandtonic/pen/dyGjvLB");
  }

  @Test
  void testCodeSandbox() {
    testEmbed("https://codesandbox.io/s/j0y0vpz59");
  }

  @Test
  void testReddit() {
    testEmbed(
        "https://reddit.com/r/java/comments/hyb41c/what_is_your_favourite_java_libraryframework/");
  }

  @Test
  void testScribd() {
    testEmbed("https://www.scribd.com/document/357546412/Opinion-on-Sarah-Palin-lawsuit");
  }

  @Test
  void testSoundcloud() {
    testEmbed("https://soundcloud.com/pslwave/drowning");
  }

  @Test
  void testTwitter() {
    testEmbed("https://twitter.com/elonmusk/status/1284291528328790016");
  }

  @Test
  void testVimeo() {
    testEmbed("https://vimeo.com/437808118");
  }

  @Test
  void testYoutubeEmbed() {
    testEmbed("https://www.youtube.com/watch?v=Nnu1E5Kslig");
  }

  private void testEmbed(String url) {
    attachment = Attachment.builder().build();
    embedService.processEmbedded(url, attachment);
    assertThat(attachment.getEmbedData()).isNotNull();

    var embedData = attachment.getEmbedData();
    assertThat(embedData.getRenderedHtml()).isNotBlank();
    assertThat(embedData.getUploaderName()).isNotBlank();
    assertThat(embedData.getEmbedUrl()).isNotBlank();
    assertThat(embedData.getProviderName()).isNotBlank();
    assertThat(embedData.getThumbnailUrl()).isNotBlank();
  }
}
