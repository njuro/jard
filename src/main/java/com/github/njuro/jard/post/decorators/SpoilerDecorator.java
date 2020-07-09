package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.SPOILER_END;
import static com.github.njuro.jard.common.Constants.SPOILER_PATTERN;
import static com.github.njuro.jard.common.Constants.SPOILER_START;

import com.github.njuro.jard.post.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

/**
 * Decorator for marking text between {@code [spoiler]} and {@code [/spoiler]} tags or between pair
 * of "{@code **}" as spoiler.
 */
@Component
public class SpoilerDecorator implements PostDecorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = SPOILER_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(SPOILER_START + "${content}" + SPOILER_END));
  }
}
