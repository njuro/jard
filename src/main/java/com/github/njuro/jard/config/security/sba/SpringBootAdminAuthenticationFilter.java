package com.github.njuro.jard.config.security.sba;

import static com.github.njuro.jard.common.Constants.SBA_SECRET_HEADER;

import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.user.UserAuthority;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for authentication between Spring Boot Admin server and client instances. Secret key is
 * stored in HTTP header with name defined in {@link Constants#SBA_SECRET_HEADER}. On successful
 * extraction and validation of secret key, fictional "user" with name is authenticated with single
 * authority - {@link UserAuthority#ACTUATOR_ACCESS} enabling him to access Spring Actuator
 * endpoints.
 */
@Component
@Slf4j
@ConditionalOnProperty(
    name = "spring.boot.admin.client.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SpringBootAdminAuthenticationFilter extends OncePerRequestFilter {

  @Value("${spring.boot.admin.context-path}")
  private String sbaContextPath;

  @Value("${management.endpoints.web.base-path}")
  private String actuatorBasePath;

  @Value("${app.sba.secret}")
  private String sbaSecret;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(actuatorBasePath)
        && !request.getRequestURI().startsWith(sbaContextPath);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String secret = request.getHeader(SBA_SECRET_HEADER);
      if (secret != null && secret.equals(sbaSecret)) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                "SBA_USER", null, Collections.singletonList(UserAuthority.ACTUATOR_ACCESS));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ex) {
      logger.error("Error getting SBA secret code: " + ex.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
