package com.github.njuro.jboard.decorators;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_START;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DecoratorTest {

    @Test
    public void testGreentextDecorator() {
        String[] validInputs = {
                ">text", ">  text", "    >longer text > another", "  > some longer text", ">>>multiple quotes",
                ">multiline\r\n>text"
        };

        String[] invalidInputs = {
                "aaa>text", "    aaaa > text", " aaa>text "
        };

        GreentextDecorator decorator = new GreentextDecorator();

        for (String input : validInputs) {
            String output = decorator.decorate(input);
            log.info(input + " -> " + output);
            assertThat(output).containsSubsequence(GREENTEXT_START, GREENTEXT_END);
        }

        for (String input : invalidInputs) {
            String output = decorator.decorate(input);
            log.info(input + " -> " + output);
            assertThat(decorator.decorate(input)).isEqualTo(input);
        }

    }
}
