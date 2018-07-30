package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.njuro.jboard.helpers.Constants.CODE_END;
import static com.github.njuro.jboard.helpers.Constants.CODE_START;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CodeDecoratorTest {

    private CodeDecorator decorator;
    private Post post;

    @BeforeEach
    void initAll() {
        decorator = new CodeDecorator();
        post = Post.builder().build();
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

    private void decoratePost(String input) {
        post.setBody(input);
        decorator.decorate(post);
        log.info(input + " -> " + post.getBody());
    }
}
