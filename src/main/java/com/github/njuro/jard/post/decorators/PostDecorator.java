package com.github.njuro.jard.post.decorators;

import com.github.njuro.jard.post.Post;

/**
 * Interface for decorating post content based on defined rules.
 *
 * @author njuro
 */
@FunctionalInterface
public interface PostDecorator {

  void decorate(Post post);
}
