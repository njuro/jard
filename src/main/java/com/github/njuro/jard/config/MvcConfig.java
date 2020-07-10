package com.github.njuro.jard.config;

import static com.jfilter.FilterConstantsHelper.MEDIA_SUB_TYPE_JSON;
import static com.jfilter.FilterConstantsHelper.MEDIA_SUB_TYPE_JSON2;
import static com.jfilter.FilterConstantsHelper.MEDIA_TYPE_APPLICATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import com.github.njuro.jard.utils.validation.ValidationMessageInterpolator;
import com.jfilter.EnableJsonFilter;
import com.jfilter.components.FilterConfiguration;
import java.nio.charset.Charset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJsonFilter
@EnableSpringDataWebSupport
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

  private final List<PathVariableArgumentResolver> pathVariableArgumentResolvers;
  private final MessageSource messageSource;

  @Value("${client.base.url:localhost}")
  private String clientBaseUrl;

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
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(Mappings.API_ROOT_USERCONTENT + "/**")
        .addResourceLocations("file:" + Constants.USER_CONTENT_PATH.toAbsolutePath() + "/")
        .setCacheControl(CacheControl.noCache());
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(clientBaseUrl, "http://localhost:3000", "http://192.168.0.80:3000")
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
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.addAll(pathVariableArgumentResolvers);
  }

  @Override
  @Bean
  @Primary
  public LocalValidatorFactoryBean getValidator() {
    var bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource);
    bean.setMessageInterpolator(new ValidationMessageInterpolator(messageSource));

    return bean;
  }
}
