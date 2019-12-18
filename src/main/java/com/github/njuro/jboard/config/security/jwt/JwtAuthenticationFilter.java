package com.github.njuro.jboard.config.security.jwt;

import com.github.njuro.jboard.common.Constants;
import com.github.njuro.jboard.user.UserFacade;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserFacade userFacade;

  @Autowired
  public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, @Lazy UserFacade userFacade) {
    this.tokenProvider = tokenProvider;
    this.userFacade = userFacade;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        String username = tokenProvider.getUsernameFromJWT(jwt);

        UserDetails userDetails = userFacade.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(Constants.JWT_COOKIE_NAME))
        .map(Cookie::getValue)
        .findAny()
        .orElse(null);
  }
}
