package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.GREENTEXT_END;
import static com.github.njuro.jard.common.Constants.GREENTEXT_PATTERN;
import static com.github.njuro.jard.common.Constants.GREENTEXT_START;

import com.github.njuro.jard.post.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

/**
 * Decorator for marking lines starting with "{@code >}" as <a
 * href="https://knowyourmeme.com/memes/greentext-stories">greentext</a>.
 */
@Component
public class GreentextDecorator implements PostDecorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = GREENTEXT_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(GREENTEXT_START + "$0" + GREENTEXT_END));
  }
}
