package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.CODE_END;
import static com.github.njuro.jboard.helpers.Constants.CODE_PATTERN;
import static com.github.njuro.jboard.helpers.Constants.CODE_START;

import com.github.njuro.jboard.models.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class CodeDecorator implements Decorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = CODE_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(CODE_START + "${content}" + CODE_END));
  }
}
