package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.models.Post;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

import static com.github.njuro.jboard.helpers.Constants.*;


@Component
public class GreentextDecorator implements Decorator {

    @Override
    public void decorate(Post post) {
        Matcher matcher = GREENTEXT_PATTERN.matcher(post.getBody());
        post.setBody(matcher.replaceAll(GREENTEXT_START + "$0" + GREENTEXT_END));
    }
}
