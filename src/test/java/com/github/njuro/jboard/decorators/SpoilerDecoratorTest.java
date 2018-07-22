package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.njuro.jboard.helpers.Constants.SPOILER_END;
import static com.github.njuro.jboard.helpers.Constants.SPOILER_START;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpoilerDecoratorTest {

    private SpoilerDecorator decorator;
    private Post post;

    @BeforeAll
    void initAll() {
        decorator = new SpoilerDecorator();
        post = Post.builder().build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"[spoiler]text[spoiler]", "**text**", "[SPOILER]text[spoiler]", "**multiple** \n [spoiler]spoilers[spoiler]"})
    void testValidSpoiler(String input) {
        decoratePost(input);
        assertThat(post.getBody()).containsSubsequence(SPOILER_START, SPOILER_END);
    }


    @ParameterizedTest
    @ValueSource(strings = {"[spoiler]text", "**text", "*****", "[spoiler][spoiler]", "**text[spoiler]", "**  **", "text"})
    void testInvalidSpoiler(String input) {
        decoratePost(input);
        assertThat(post.getBody()).isEqualTo(input);
    }

    private void decoratePost(String input) {
        post.setBody(input);
        decorator.decorate(post);
        log.info(input + " -> " + post.getBody());
    }
}
