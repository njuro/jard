package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.exceptions.PostNotFoundException;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;

import static com.github.njuro.jboard.helpers.Constants.*;

@Component
public class CrosslinkDecorator implements Decorator {

    private final PostService postService;

    @Autowired
    public CrosslinkDecorator(PostService postService) {
        this.postService = postService;
    }


    @Override
    public void decorate(Post post) {
        Matcher matcher = CROSSLINK_PATTERN.matcher(post.getBody());
        StringBuffer sb = new StringBuffer(post.getBody().length());

        while (matcher.find()) {
            String boardLabel = Optional.ofNullable(matcher.group("board")).orElse(post.getThread().getBoard().getLabel());
            long postNumber = Long.valueOf(matcher.group("postNo"));

            Post linkedPost = null;
            boolean valid = true;

            try {
                linkedPost = postService.resolvePost(boardLabel, postNumber);
            } catch (PostNotFoundException e) {
                valid = false;
            }

            String linkClass = valid ? CROSSLINK_CLASS_VALID : CROSSLINK_CLASS_INVALID;
            String linkHref = valid ? "/board/" + boardLabel + "/" + linkedPost.getThread().getPostNumber() + "#" + linkedPost.getPostNumber() : "#";
            String crosslinkStart = CROSSLINK_START.replace("${linkHref}", linkHref).replace("${linkClass}", linkClass);

            matcher.appendReplacement(sb, crosslinkStart + "$0" + CROSSLINK_END);
        }

        matcher.appendTail(sb);
        post.setBody(sb.toString());
    }
}
