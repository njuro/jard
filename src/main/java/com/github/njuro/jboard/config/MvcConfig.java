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
      final BoardResolver boardResolver,
      final ThreadResolver threadResolver,
      final PostResolver postResolver) {
    this.boardResolver = boardResolver;
    this.threadResolver = threadResolver;
    this.postResolver = postResolver;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  public static void configureJsonFilter(
      final FilterConfiguration filterConfiguration, final ObjectMapper objectMapper) {
    filterConfiguration.setMapper(APPLICATION_JSON, objectMapper);
    filterConfiguration.setMapper(
        new MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON, Charset.defaultCharset()),
        objectMapper);
    filterConfiguration.setMapper(
        new MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON2), objectMapper);
  }

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:3000", "http://192.168.0.80:3000")
        .allowCredentials(true);
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(Constants.USER_CONTENT_URL + "**")
        .addResourceLocations(Constants.USER_CONTENT_PATH.toUri().toString());
  }

  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(boardResolver);
    resolvers.add(threadResolver);
    resolvers.add(postResolver);
  }
}
