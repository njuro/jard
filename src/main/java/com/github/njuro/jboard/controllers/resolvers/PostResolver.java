package com.github.njuro.jboard.controllers.resolvers;

import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver for mapping post number and board label (path variables) to respective {@link Post} object
 *
 * @author njuro
 */
@Component
public class PostResolver implements PathVariableArgumentResolver {

    private final PostService postService;

    @Autowired
    public PostResolver(PostService postService) {
        this.postService = postService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Post.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Long postNumber = Long.valueOf(getPathVariable("postNo", webRequest));
        String board = getPathVariable("board", webRequest);

        return postService.resolvePost(board, postNumber);
    }
}
