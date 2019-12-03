package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_START;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GreentextDecoratorTest extends DecoratorTest {

  @Override
  protected Decorator initDecorator() {
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
  void testValidGreentext(final String input) {
    decoratePost(input);
    assertThat(DecoratorTest.post.getBody()).containsSubsequence(GREENTEXT_START, GREENTEXT_END);
  }

  @ParameterizedTest
  @ValueSource(strings = {"aaa>text", "    aaaa > text", " aaa>text "})
  void testInvalidGreentext(final String input) {
    decoratePost(input);
    assertThat(DecoratorTest.post.getBody()).isEqualTo(input);
  }
}
