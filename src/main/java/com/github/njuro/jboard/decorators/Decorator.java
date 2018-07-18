package com.github.njuro.jboard.decorators;

/**
 * Interface for decorating text input based on defined rules.
 *
 * @author njuro
 */
@FunctionalInterface
public interface Decorator {

    String decorate(String text);
}
