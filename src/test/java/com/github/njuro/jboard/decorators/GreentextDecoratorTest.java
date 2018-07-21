package com.github.njuro.jboard.decorators;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_START;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GreentextDecoratorTest {

    @ParameterizedTest
    @ValueSource(strings = {">text", ">  text", "    >longer text > another", "  > some longer text", ">>>multiple quotes",
            ">multiline\r\n>text"})
    void testValidGreentext(String input) {
        GreentextDecorator decorator = new GreentextDecorator();
        assertThat(decorator.decorate(input)).containsSubsequence(GREENTEXT_START, GREENTEXT_END);
    }


    @ParameterizedTest
    @ValueSource(strings = {"aaa>text", "    aaaa > text", " aaa>text "})
    void testInvalidGreentext(String input) {
        GreentextDecorator decorator = new GreentextDecorator();
        assertThat(decorator.decorate(input)).isEqualTo(input);
    }
}
