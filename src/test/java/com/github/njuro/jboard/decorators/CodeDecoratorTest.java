package com.github.njuro.jboard.decorators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.njuro.jboard.helpers.Constants.CODE_END;
import static com.github.njuro.jboard.helpers.Constants.CODE_START;
import static org.assertj.core.api.Assertions.assertThat;

class CodeDecoratorTest extends DecoratorTest {

    @Override
    protected Decorator initDecorator() {
        return new CodeDecorator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"[code]text[/code]", "[CODE]text[/code]", "[code]\nmultiple\n[/code] \n [code]code blocks[/code]"})
    void testValidCodeBlock(String input) {
        decoratePost(input);
        assertThat(post.getBody()).containsSubsequence(CODE_START, CODE_END);
    }


    @ParameterizedTest
    @ValueSource(strings = {"[code]text", "[code]text[code]", "test[/code]", "[code] [/code]", "text"})
    void testInvalidCodeBlock(String input) {
        decoratePost(input);
        assertThat(post.getBody()).isEqualTo(input);
    }

}
