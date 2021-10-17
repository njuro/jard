package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.*;

import com.github.njuro.jard.post.Post;
import java.util.regex.Matcher;
import org.springframework.stereotype.Component;

/** Decorator for creating navigable HTML anchors from valid URLs (starting with http/https). */
@Component
public class HyperlinkDecorator implements PostDecorator {

  @Override
  public void decorate(Post post) {
    Matcher matcher = HYPERLINK_PATTERN.matcher(post.getBody());
    var sb = new StringBuilder(post.getBody().length());
    while (matcher.find()) {
      String href = matcher.group("href");
      matcher.appendReplacement(
          sb, HYPERLINK_START.replace("${href}", href) + "$0" + HYPERLINK_END);
    }

    matcher.appendTail(sb);
    post.setBody(sb.toString());
  }
}
