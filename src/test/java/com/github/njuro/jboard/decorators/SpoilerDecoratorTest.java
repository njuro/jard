package com.github.njuro.jboard.decorators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.njuro.jboard.helpers.Constants.SPOILER_END;
import static com.github.njuro.jboard.helpers.Constants.SPOILER_START;
import static org.assertj.core.api.Assertions.assertThat;

class SpoilerDecoratorTest extends DecoratorTest {

    @Override
    protected Decorator initDecorator() {
        return new SpoilerDecorator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"[spoiler]text[spoiler]", "**text**", "[SPOILER]text[spoiler]", "**multiple** \n [spoiler]\nspoilers\n[spoiler]"})
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
}
