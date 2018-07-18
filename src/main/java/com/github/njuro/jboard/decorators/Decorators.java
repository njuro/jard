package com.github.njuro.jboard.decorators;

public class Decorators {

    private static final Decorator[] DECORATORS = {new GreentextDecorator()};


    public static String decorate(String text) {
        String output = text;

        for (Decorator decorator : DECORATORS) {
            output = decorator.decorate(text);
        }

        return text;
    }
}
