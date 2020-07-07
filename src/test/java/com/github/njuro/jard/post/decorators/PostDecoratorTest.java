package com.github.njuro.jard.post.decorators;

import com.github.njuro.jard.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public abstract class PostDecoratorTest {

  protected static PostDecorator decorator;
  protected static Post post;

  @BeforeAll
  public void initAll() {
    post = Post.builder().build();
    decorator = initDecorator();
  }

  protected abstract PostDecorator initDecorator();

  protected void decoratePost(String body) {
    decoratePost(post, body);
  }

  protected void decoratePost(Post post, String body) {
    post.setBody(body);
    decorator.decorate(post);
    log.info(body + " -> " + post.getBody());
  }
}
