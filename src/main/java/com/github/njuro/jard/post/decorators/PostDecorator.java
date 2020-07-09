package com.github.njuro.jard.post.decorators;

import com.github.njuro.jard.post.Post;

/** Interface for decorating {@link Post} (more precisely its body). */
@FunctionalInterface
public interface PostDecorator {

  /**
   * Decorates given post. Should not change any of post fields, except {@code body}. Example being
   * rendering of special tags inside body etc.
   *
   * @param post post to decorate
   */
  void decorate(Post post);
}
