package com.github.njuro.config;

import com.github.njuro.helpers.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constants.USER_CONTENT_URL + "**")
                .addResourceLocations(Constants.USER_CONTENT_PATH.toUri().toString());
    }

}
