package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_START;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GreentextDecoratorTest {

    private GreentextDecorator decorator;
    private Post post;

    @BeforeAll
    void initAll() {
        decorator = new GreentextDecorator();
        post = Post.builder().build();
    }

    @ParameterizedTest
    @ValueSource(strings = {">text", ">  text", "    >longer text > another", "  > some longer text", ">>>multiple quotes",
            ">multiline\r\n>text"})
    void testValidGreentext(String input) {
        decoratePost(input);
        assertThat(post.getBody()).containsSubsequence(GREENTEXT_START, GREENTEXT_END);
    }


    @ParameterizedTest
    @ValueSource(strings = {"aaa>text", "    aaaa > text", " aaa>text "})
    void testInvalidGreentext(String input) {
        decoratePost(input);
        assertThat(post.getBody()).isEqualTo(input);
    }

    private void decoratePost(String input) {
        post.setBody(input);
        decorator.decorate(post);
        log.info(input + " -> " + post.getBody());
    }
}
