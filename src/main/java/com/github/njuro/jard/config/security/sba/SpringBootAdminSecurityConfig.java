package com.github.njuro.jard.config.security.sba;

import static com.github.njuro.jard.common.Constants.SBA_SECRET_HEADER;

import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/** Configuration of authentication between Spring Boot Admin server and client instances. */
@Configuration
@ConditionalOnProperty(
    name = "spring.boot.admin.client.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SpringBootAdminSecurityConfig {

  /** Properties of Spring Boot Admin client. */
  private final ClientProperties clientProperties;

  /** Spring Boot Admin secret (used for authentication). */
  @Value("${app.sba.secret}")
  private String sbaSecret;

  public SpringBootAdminSecurityConfig(ClientProperties clientProperties) {
    this.clientProperties = clientProperties;
  }

  /** Adds header with Spring Boot Admin secret to all requests from SBA server to client. */
  @Bean
  public HttpHeadersProvider httpHeadersProvider() {
    return (instance -> {
      var httpHeaders = new HttpHeaders();
      httpHeaders.add(SBA_SECRET_HEADER, sbaSecret);
      return httpHeaders;
    });
  }

  /**
   * Adds header with Spring Boot Admin secret to registration request from SBA client to server.
   */
  @Bean
  public BlockingRegistrationClient registrationClient() {
    RestTemplate template =
        new RestTemplateBuilder()
            .setConnectTimeout(clientProperties.getConnectTimeout())
            .setReadTimeout(clientProperties.getReadTimeout())
            .build();

    return new BlockingRegistrationClient(template) {
      @NotNull
      @Override
      protected HttpHeaders createRequestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(SBA_SECRET_HEADER, sbaSecret);
        return HttpHeaders.readOnlyHttpHeaders(headers);
      }
    };
  }
}
