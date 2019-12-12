package com.github.njuro.jboard.post.decorators;

import static com.github.njuro.jboard.common.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.common.Constants.GREENTEXT_START;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GreentextDecoratorTest extends PostDecoratorTest {

  @Override
  protected PostDecorator initDecorator() {
    return new GreentextDecorator();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ">text",
        ">  text",
        "    >longer text > another",
        "  > some longer text",
        ">>>multiple quotes",
        ">multiline\r\n>text"
      })
  void testValidGreentext(String input) {
    decoratePost(input);
    assertThat(PostDecoratorTest.post.getBody())
        .containsSubsequence(GREENTEXT_START, GREENTEXT_END);
  }

  @ParameterizedTest
  @ValueSource(strings = {"aaa>text", "    aaaa > text", " aaa>text "})
  void testInvalidGreentext(String input) {
    decoratePost(input);
    assertThat(PostDecoratorTest.post.getBody()).isEqualTo(input);
  }
}
