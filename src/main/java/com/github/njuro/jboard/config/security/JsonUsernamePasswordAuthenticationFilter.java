package com.github.njuro.jboard.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jboard.helpers.Constants;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  @Autowired private ObjectMapper objectMapper;

  @Override
  public Authentication attemptAuthentication(
      final HttpServletRequest request, final HttpServletResponse response)
      throws AuthenticationException {
    try {
      final LoginRequest login =
          objectMapper.readValue(request.getReader(), LoginRequest.class);
      final UsernamePasswordAuthenticationToken authRequest =
          new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());

      setDetails(request, authRequest);
      request.setAttribute(Constants.JWT_REMEMBER_ME_ATTRIBUTE, login.isRememberMe());
      return getAuthenticationManager().authenticate(authRequest);
    } catch (final IOException e) {
      throw new InternalAuthenticationServiceException(
          "Parsing login request failed: " + e.getMessage());
    }
  }

  @Getter
  private static class LoginRequest {

    private String username;
    private String password;
    private boolean rememberMe;
  }
}
