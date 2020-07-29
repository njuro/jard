package com.github.njuro.jard.config.security;

import com.github.njuro.jard.utils.HttpUtils;
import com.github.njuro.jard.utils.validation.ValidationErrors;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final HttpUtils httpUtils;

  @Autowired
  public LoginFailureHandler(HttpUtils httpUtils) {
    this.httpUtils = httpUtils;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    httpUtils.writeJsonToResponse(
        response, new ValidationErrors("Authentication failed: " + exception.getMessage()));
  }
}
