package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.CROSSLINK_CLASS_INVALID;
import static com.github.njuro.jard.common.Constants.CROSSLINK_CLASS_VALID;
import static com.github.njuro.jard.common.Constants.CROSSLINK_DIFF_THREAD;
import static com.github.njuro.jard.common.Constants.CROSSLINK_END;
import static com.github.njuro.jard.common.Constants.CROSSLINK_OP;
import static com.github.njuro.jard.common.Constants.CROSSLINK_PATTERN;
import static com.github.njuro.jard.common.Constants.CROSSLINK_START;

import com.github.njuro.jard.board.BoardNotFoundException;
import com.github.njuro.jard.board.BoardService;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.post.PostNotFoundException;
import com.github.njuro.jard.post.PostService;
import java.util.Optional;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Decorator for rendering words starting with "{@code >>}" or "{@code >>>}" as links to other
 * active posts/threads/boards and checking if the linked entity exists.
 */
@Component
public class CrosslinkDecorator implements PostDecorator {

  private final BoardService boardService;
  private final PostService postService;

  @Lazy
  @Autowired
  public CrosslinkDecorator(BoardService boardService, PostService postService) {
    this.boardService = boardService;
    this.postService = postService;
  }

  @Override
  public void decorate(Post post) {
    Matcher matcher = CROSSLINK_PATTERN.matcher(post.getBody());
    StringBuffer sb = new StringBuffer(post.getBody().length());

    while (matcher.find()) {
      String boardLabel =
          Optional.ofNullable(matcher.group("board"))
              .orElse(post.getThread().getBoard().getLabel());
      String postNumber = matcher.group("postNo");

      String linkHref = "/boards/" + boardLabel;
      boolean valid = true;
      String special = "";

      try {
        if (postNumber == null || postNumber.isEmpty()) {
          boardService.resolveBoard(boardLabel);
        } else {
          Post linkedPost = postService.resolvePost(boardLabel, Long.valueOf(postNumber));
          if (linkedPost.equals(post.getThread().getOriginalPost())) {
            special += CROSSLINK_OP;
          }
          if (!linkedPost.getThread().equals(post.getThread())) {
            special += CROSSLINK_DIFF_THREAD;
          }
          linkHref +=
              "/thread/"
                  + linkedPost.getThread().getThreadNumber()
                  + "#"
                  + linkedPost.getPostNumber();
        }
      } catch (BoardNotFoundException | PostNotFoundException e) {
        valid = false;
      }

      String linkClass = valid ? CROSSLINK_CLASS_VALID : CROSSLINK_CLASS_INVALID;
      String crosslinkStart =
          CROSSLINK_START
              .replace("${linkHref}", valid ? linkHref : "#")
              .replace("${linkClass}", linkClass);

      matcher.appendReplacement(sb, crosslinkStart + "$0" + " " + special + CROSSLINK_END);
    }

    matcher.appendTail(sb);
    post.setBody(sb.toString());
  }
}
