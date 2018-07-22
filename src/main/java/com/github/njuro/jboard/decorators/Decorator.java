package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;

/**
 * Interface for decorating post content based on defined rules.
 *
 * @author njuro
 */
@FunctionalInterface
public interface Decorator {

    void decorate(Post post);
}
