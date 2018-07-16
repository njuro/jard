package com.github.njuro.jboard.config;

import com.github.njuro.jboard.controllers.resolvers.BoardResolver;
import com.github.njuro.jboard.controllers.resolvers.PostResolver;
import com.github.njuro.jboard.controllers.resolvers.ThreadResolver;
import com.github.njuro.jboard.helpers.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Custom Spring MVC configuration, which handles these processes:
 *
 * <ul>
 * <li>maps files from {@link Constants#USER_CONTENT_PATH}</li> to {@link Constants#USER_CONTENT_URL}
 * <li>registers custom method argument resolvers</li>
 * </ul>
 *
 * @author njuro
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final BoardResolver boardResolver;
    private final ThreadResolver threadResolver;
    private final PostResolver postResolver;

    public MvcConfig(BoardResolver boardResolver, ThreadResolver threadResolver, PostResolver postResolver) {
        this.boardResolver = boardResolver;
        this.threadResolver = threadResolver;
        this.postResolver = postResolver;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constants.USER_CONTENT_URL + "**")
                .addResourceLocations(Constants.USER_CONTENT_PATH.toUri().toString());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(boardResolver);
        resolvers.add(threadResolver);
        resolvers.add(postResolver);
    }
}
