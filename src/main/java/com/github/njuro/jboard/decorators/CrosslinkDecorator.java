package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_CLASS_INVALID;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_CLASS_VALID;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_DIFF_THREAD;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_END;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_OP;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_PATTERN;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_START;

import com.github.njuro.jboard.exceptions.BoardNotFoundException;
import com.github.njuro.jboard.exceptions.PostNotFoundException;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.services.BoardService;
import com.github.njuro.jboard.services.PostService;
import java.util.Optional;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CrosslinkDecorator implements Decorator {

  private final BoardService boardService;
  private final PostService postService;

  @Lazy
  @Autowired
  public CrosslinkDecorator(final BoardService boardService, final PostService postService) {
    this.boardService = boardService;
    this.postService = postService;
  }

  @Override
  public void decorate(final Post post) {
    final Matcher matcher = CROSSLINK_PATTERN.matcher(post.getBody());
    final StringBuffer sb = new StringBuffer(post.getBody().length());

    while (matcher.find()) {
      final String boardLabel =
          Optional.ofNullable(matcher.group("board"))
              .orElse(post.getThread().getBoard().getLabel());
      final String postNumber = matcher.group("postNo");

      String linkHref = "/boards/" + boardLabel;
      boolean valid = true;
      String special = "";

      try {
        if (postNumber == null || postNumber.isEmpty()) {
          this.boardService.resolveBoard(boardLabel);
        } else {
          final Post linkedPost =
              this.postService.resolvePost(boardLabel, Long.valueOf(postNumber));
          if (linkedPost.equals(post.getThread().getOriginalPost())) {
            special += CROSSLINK_OP;
          }
          if (!linkedPost.getThread().equals(post.getThread())) {
            special += CROSSLINK_DIFF_THREAD;
          }
          linkHref +=
              "/" + linkedPost.getThread().getPostNumber() + "#" + linkedPost.getPostNumber();
        }
      } catch (final BoardNotFoundException | PostNotFoundException e) {
        valid = false;
      }

      final String linkClass = valid ? CROSSLINK_CLASS_VALID : CROSSLINK_CLASS_INVALID;
      final String crosslinkStart =
          CROSSLINK_START
              .replace("${linkHref}", valid ? linkHref : "#")
              .replace("${linkClass}", linkClass);

      matcher.appendReplacement(sb, crosslinkStart + "$0" + " " + special + CROSSLINK_END);
    }

    matcher.appendTail(sb);
    post.setBody(sb.toString());
  }
}
