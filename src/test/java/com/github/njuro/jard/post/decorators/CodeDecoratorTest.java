package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.CODE_END;
import static com.github.njuro.jard.common.Constants.CODE_START;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CodeDecoratorTest extends PostDecoratorTest {

  @Override
  protected PostDecorator initDecorator() {
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
    assertThat(PostDecoratorTest.post.getBody()).containsSubsequence(CODE_START, CODE_END);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"[code]text", "[code]text[code]", "test[/code]", "[code] [/code]", "text"})
  void testInvalidCodeBlock(String input) {
    decoratePost(input);
    assertThat(PostDecoratorTest.post.getBody()).isEqualTo(input);
  }
}
