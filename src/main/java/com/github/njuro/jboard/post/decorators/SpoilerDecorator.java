package com.github.njuro.jboard.post.decorators;

import static com.github.njuro.jboard.common.Constants.SPOILER_END;
import static com.github.njuro.jboard.common.Constants.SPOILER_PATTERN;
import static com.github.njuro.jboard.common.Constants.SPOILER_START;

import com.github.njuro.jboard.post.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class SpoilerDecorator implements PostDecorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = SPOILER_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(SPOILER_START + "${content}" + SPOILER_END));
  }
}
