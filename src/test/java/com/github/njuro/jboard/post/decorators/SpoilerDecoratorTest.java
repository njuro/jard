package com.github.njuro.jboard.post.decorators;

import static com.github.njuro.jboard.common.Constants.SPOILER_END;
import static com.github.njuro.jboard.common.Constants.SPOILER_START;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SpoilerDecoratorTest extends PostDecoratorTest {

  @Override
  protected PostDecorator initDecorator() {
    return new SpoilerDecorator();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[spoiler]text[spoiler]",
        "**text**",
        "[SPOILER]text[spoiler]",
        "**multiple** \n [spoiler]\nspoilers\n[spoiler]"
      })
  void testValidSpoiler(String input) {
    decoratePost(input);
    assertThat(PostDecoratorTest.post.getBody()).containsSubsequence(SPOILER_START, SPOILER_END);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[spoiler]text",
        "**text",
        "*****",
        "[spoiler][spoiler]",
        "**text[spoiler]",
        "**  **",
        "text"
      })
  void testInvalidSpoiler(String input) {
    decoratePost(input);
    assertThat(PostDecoratorTest.post.getBody()).isEqualTo(input);
  }
}
