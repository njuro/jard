package com.github.njuro.jboard.decorators;

import java.util.regex.Matcher;

import static com.github.njuro.jboard.helpers.Constants.*;

public class GreentextDecorator implements Decorator {

    @Override
    public String decorate(String text) {
        Matcher matcher = GREENTEXT_PATTERN.matcher(text);
        return matcher.replaceAll(GREENTEXT_START + "$0" + GREENTEXT_END);
    }
}
