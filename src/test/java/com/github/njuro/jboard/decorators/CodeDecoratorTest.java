package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.CODE_END;
import static com.github.njuro.jboard.helpers.Constants.CODE_START;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CodeDecoratorTest extends DecoratorTest {

  @Override
  protected Decorator initDecorator() {
    return new CodeDecorator();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[code]text[/code]",
        "[CODE]text[/code]",
        "[code]\nmultiple\n[/code] \n [code]code blocks[/code]"
      })
  void testValidCodeBlock(String input) {
    decoratePost(input);
    assertThat(DecoratorTest.post.getBody()).containsSubsequence(CODE_START, CODE_END);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"[code]text", "[code]text[code]", "test[/code]", "[code] [/code]", "text"})
  void testInvalidCodeBlock(String input) {
    decoratePost(input);
    assertThat(DecoratorTest.post.getBody()).isEqualTo(input);
  }
}
