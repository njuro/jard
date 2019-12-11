package com.github.njuro.jboard.post.decorators;

import static com.github.njuro.jboard.common.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.common.Constants.GREENTEXT_PATTERN;
import static com.github.njuro.jboard.common.Constants.GREENTEXT_START;

import com.github.njuro.jboard.post.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class GreentextDecorator implements Decorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = GREENTEXT_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(GREENTEXT_START + "$0" + GREENTEXT_END));
  }
}
