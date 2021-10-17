package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.CODE_END;
import static com.github.njuro.jard.common.Constants.CODE_PATTERN;
import static com.github.njuro.jard.common.Constants.CODE_START;

import com.github.njuro.jard.post.Post;
import org.springframework.stereotype.Component;

/**
 * Decorator for marking text between {@code [code]} and {@code [/code]} tags as block of formatted
 * code. *
 */
@Component
public class CodeDecorator implements PostDecorator {

  @Override
  public void decorate(Post post) {
    var matcher = CODE_PATTERN.matcher(post.getBody());
    post.setBody(matcher.replaceAll(CODE_START + "${content}" + CODE_END));
  }
}
