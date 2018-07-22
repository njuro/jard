package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

import static com.github.njuro.jboard.helpers.Constants.*;

@Component
public class SpoilerDecorator implements Decorator {

    @Override
    public void decorate(Post post) {
        Matcher matcher = SPOILER_PATTERN.matcher(post.getBody());
        post.setBody(matcher.replaceAll(SPOILER_START + "${content}" + SPOILER_END));
    }
}
