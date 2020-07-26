package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.HYPERLINK_END;
import static com.github.njuro.jard.common.Constants.HYPERLINK_START;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HyperlinkDecoratorTest extends PostDecoratorTest {

  @Override
  protected PostDecorator initDecorator() {
    return new HyperlinkDecorator();
  }

  @Test
  void testValidHyperlink() {
    decoratePost("text https://google.com text");
    assertThat(post.getBody())
        .containsSubsequence(
            HYPERLINK_START.substring(0, 3), "google.com", "google.com", HYPERLINK_END);
  }

  @Test
  void testMultipleValidHyperlink() {
    decoratePost("text https://google.com http://yahoo.com/something text");
    assertThat(post.getBody())
        .containsSubsequence(
            HYPERLINK_START.substring(0, 3),
            "google.com",
            "google.com",
            HYPERLINK_END,
            HYPERLINK_START.substring(0, 3),
            "yahoo.com",
            "yahoo.com",
            HYPERLINK_END);
  }

  @Test
  void testInvalidHyperlink() {
    String body = "text http://google www.google.com text";
    decoratePost(body);
    assertThat(post.getBody()).isEqualTo(body);
  }
}
