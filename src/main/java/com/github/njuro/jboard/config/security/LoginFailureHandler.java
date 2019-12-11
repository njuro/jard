package com.github.njuro.jboard.config.security;

import com.github.njuro.jboard.utils.ResponseJsonWriter;
import com.github.njuro.jboard.utils.validation.ValidationErrors;
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

  private final ResponseJsonWriter responseJsonWriter;

  @Autowired
  public LoginFailureHandler(ResponseJsonWriter responseJsonWriter) {
    this.responseJsonWriter = responseJsonWriter;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    responseJsonWriter.writeJsonToResponse(
        response, new ValidationErrors("Authentication failed: " + exception.getMessage()));
  }
}
