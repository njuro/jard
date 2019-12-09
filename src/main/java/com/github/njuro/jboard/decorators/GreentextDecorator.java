package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_END;
import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_PATTERN;
import static com.github.njuro.jboard.helpers.Constants.GREENTEXT_START;

import com.github.njuro.jboard.models.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class GreentextDecorator implements Decorator {

  @Override
  public void decorate(final Post post) {
    final Matcher matcher = GREENTEXT_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(GREENTEXT_START + "$0" + GREENTEXT_END));
  }
}
