package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.SPOILER_END;
import static com.github.njuro.jboard.helpers.Constants.SPOILER_PATTERN;
import static com.github.njuro.jboard.helpers.Constants.SPOILER_START;

import com.github.njuro.jboard.models.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class SpoilerDecorator implements Decorator {

  @Override
  public void decorate(final Post post) {
    final Matcher matcher = SPOILER_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(SPOILER_START + "${content}" + SPOILER_END));
  }
}
