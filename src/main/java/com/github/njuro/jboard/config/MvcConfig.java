package com.github.njuro.jboard.config;

import static com.jfilter.FilterConstantsHelper.MEDIA_SUB_TYPE_JSON;
import static com.jfilter.FilterConstantsHelper.MEDIA_SUB_TYPE_JSON2;
import static com.jfilter.FilterConstantsHelper.MEDIA_TYPE_APPLICATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jboard.controllers.resolvers.BoardResolver;
import com.github.njuro.jboard.controllers.resolvers.PostResolver;
import com.github.njuro.jboard.controllers.resolvers.ThreadResolver;
import com.github.njuro.jboard.helpers.Constants;
import com.jfilter.EnableJsonFilter;
import com.jfilter.components.FilterConfiguration;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJsonFilter
@EnableSpringDataWebSupport
public class MvcConfig implements WebMvcConfigurer {

  private final BoardResolver boardResolver;
  private final ThreadResolver threadResolver;
  private final PostResolver postResolver;

  public MvcConfig(
      BoardResolver boardResolver, ThreadResolver threadResolver, PostResolver postResolver) {
    this.boardResolver = boardResolver;
    this.threadResolver = threadResolver;
    this.postResolver = postResolver;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  public void configureJsonFilter(
      FilterConfiguration filterConfiguration, ObjectMapper objectMapper) {
    filterConfiguration.setMapper(APPLICATION_JSON, objectMapper);
    filterConfiguration.setMapper(
        new MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON, Charset.defaultCharset()),
        objectMapper);
    filterConfiguration.setMapper(
        new MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON2), objectMapper);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:3000", "http://192.168.0.80:3000")
        .allowedMethods(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.OPTIONS.name())
        .allowCredentials(true);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(Constants.USER_CONTENT_URL + "**")
        .addResourceLocations(Constants.USER_CONTENT_PATH.toUri().toString());
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(boardResolver);
    resolvers.add(threadResolver);
    resolvers.add(postResolver);
  }
}
