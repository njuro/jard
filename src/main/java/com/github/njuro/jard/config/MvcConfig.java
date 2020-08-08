package com.github.njuro.jard.config;

import static com.jfilter.FilterConstantsHelper.*;
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
import org.apache.catalina.Context;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
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

/** Configuration of Spring MVC. */
@Configuration
@EnableJsonFilter
@EnableSpringDataWebSupport // for resolving of parameters in controllers, such as Pageable
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

  private final List<PathVariableArgumentResolver> pathVariableArgumentResolvers;
  private final MessageSource messageSource;

  @Value("${client.base.url:localhost}")
  private String clientBaseUrl;

  @Value("${server.base.url:localhost}")
  private String serverBaseUrl;

  @Value("${DISABLE_CSRF_PROTECTION:false}")
  private boolean disableCsrfProtection;

  /** Sets JSON {@link ObjectMapper} for jfilter filter classes. */
  @Autowired
  public void configureJsonFilter(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          FilterConfiguration filterConfiguration,
      ObjectMapper objectMapper) {
    filterConfiguration.setMapper(APPLICATION_JSON, objectMapper);
    filterConfiguration.setMapper(
        new MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON, Charset.defaultCharset()),
        objectMapper);
    filterConfiguration.setMapper(
        new MediaType(MEDIA_TYPE_APPLICATION, MEDIA_SUB_TYPE_JSON2), objectMapper);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // serve content of local user content folder on user content endpoint
    // useful mostly for local development
    registry
        .addResourceHandler(Mappings.API_ROOT_USERCONTENT + "/**")
        .addResourceLocations("file:" + Constants.USER_CONTENT_PATH.toAbsolutePath() + "/")
        .setCacheControl(CacheControl.noCache());
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(
            clientBaseUrl,
            serverBaseUrl,
            "http://localhost:3000",
            "http://192.168.0.80:3000",
            "http://192.168.0.106:3000")
        .allowedMethods(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.OPTIONS.name())
        .allowCredentials(true); // for CSRF protection
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.addAll(pathVariableArgumentResolvers);
  }

  @Override
  @Bean
  @Primary
  public LocalValidatorFactoryBean getValidator() {
    // register custom bean validator which loads validation messages from messages.properties
    // resource file and optionally do custom interpolation of variables in them
    var bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource);
    bean.setMessageInterpolator(new ValidationMessageInterpolator(messageSource));

    return bean;
  }

  /** Enables Spring Actuator /httptrace endpoint for seeing most recent requests */
  @Bean
  public HttpTraceRepository httpTraceRepository() {
    return new InMemoryHttpTraceRepository();
  }

  @Bean
  @Primary
  public CookieProcessor cookieProcessor() {
    var rfc6265CookieProcessor = new Rfc6265CookieProcessor();
    var sameSitePolicy = disableCsrfProtection ? SameSiteCookies.NONE : SameSiteCookies.STRICT;
    rfc6265CookieProcessor.setSameSiteCookies(sameSitePolicy.getValue());
    return rfc6265CookieProcessor;
  }

  @Bean
  public ServletWebServerFactory servletContainer() {
    return new TomcatServletWebServerFactory() {
      @Override
      protected void postProcessContext(Context context) {
        context.setCookieProcessor(cookieProcessor());
      }
    };
  }
}
