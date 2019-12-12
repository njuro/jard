package com.github.njuro.jboard.post.decorators;

import static com.github.njuro.jboard.common.Constants.CODE_END;
import static com.github.njuro.jboard.common.Constants.CODE_PATTERN;
import static com.github.njuro.jboard.common.Constants.CODE_START;

import com.github.njuro.jboard.post.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

@Component
public class CodeDecorator implements PostDecorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = CODE_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(CODE_START + "${content}" + CODE_END));
  }
}
