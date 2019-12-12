package com.github.njuro.jboard.post.decorators;

import com.github.njuro.jboard.post.Post;

/**
 * Interface for decorating post content based on defined rules.
 *
 * @author njuro
 */
@FunctionalInterface
public interface PostDecorator {

  void decorate(Post post);
}
