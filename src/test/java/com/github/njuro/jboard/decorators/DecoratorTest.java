package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public abstract class DecoratorTest {

  protected static Decorator decorator;
  protected static Post post;

  @BeforeAll
  public void initAll() {
    post = Post.builder().build();
    decorator = initDecorator();
  }

  protected abstract Decorator initDecorator();

  protected void decoratePost(final String body) {
    decoratePost(post, body);
  }

  protected void decoratePost(final Post post, final String body) {
    post.setBody(body);
    decorator.decorate(post);
    log.info(body + " -> " + post.getBody());
  }
}
