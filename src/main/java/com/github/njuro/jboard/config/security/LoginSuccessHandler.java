package com.github.njuro.jboard.config.security;

import com.github.njuro.jboard.config.security.jwt.JwtTokenProvider;
import com.github.njuro.jboard.controllers.utils.ResponseJsonWriter;
import com.github.njuro.jboard.facades.UserFacade;
import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.User;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserFacade userFacade;
  private final JwtTokenProvider jwtTokenProvider;
  private final ResponseJsonWriter responseJsonWriter;

  @Autowired
  public LoginSuccessHandler(
      final UserFacade userFacade,
      final JwtTokenProvider jwtTokenProvider,
      final ResponseJsonWriter responseJsonWriter) {
    this.userFacade = userFacade;
    this.jwtTokenProvider = jwtTokenProvider;
    this.responseJsonWriter = responseJsonWriter;
    setRedirectStrategy(new NoRedirectStrategy());
  }

  @Override
  public void onAuthenticationSuccess(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Authentication authentication)
      throws IOException, ServletException {
    final User user = (User) authentication.getPrincipal();
    user.setLastLoginIp(request.getRemoteAddr());
    user.setLastLogin(LocalDateTime.now());
    userFacade.updateUser(user);
    log.debug("User {} logged from IP {}", user.getUsername(), user.getLastLoginIp());

    super.onAuthenticationSuccess(request, response, authentication);

    final boolean rememberMe =
        Boolean.parseBoolean(request.getAttribute(Constants.JWT_REMEMBER_ME_ATTRIBUTE).toString());
    response.addCookie(
        rememberMe
            ? jwtTokenProvider.generateRememberMeCookie(authentication)
            : jwtTokenProvider.generateSessionCookie(authentication));
    responseJsonWriter.writeJsonToResponse(response, UserFacade.getCurrentUser());
  }
}
