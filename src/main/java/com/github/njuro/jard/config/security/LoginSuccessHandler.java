package com.github.njuro.jard.config.security;

import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.config.security.jwt.JwtTokenProvider;
import com.github.njuro.jard.user.User;
import com.github.njuro.jard.user.UserService;
import com.github.njuro.jard.utils.HttpUtils;
import java.io.IOException;
import java.time.OffsetDateTime;
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

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final HttpUtils httpUtils;

  @Autowired
  public LoginSuccessHandler(
      UserService userService, JwtTokenProvider jwtTokenProvider, HttpUtils httpUtils) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.httpUtils = httpUtils;
    setRedirectStrategy(new NoRedirectStrategy());
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    User user = (User) authentication.getPrincipal();
    user.setLastLoginIp(request.getRemoteAddr());
    user.setLastLogin(OffsetDateTime.now());
    userService.saveUser(user);
    log.debug("User {} logged from IP {}", user.getUsername(), user.getLastLoginIp());

    super.onAuthenticationSuccess(request, response, authentication);

    boolean rememberMe =
        Boolean.parseBoolean(request.getAttribute(Constants.JWT_REMEMBER_ME_ATTRIBUTE).toString());
    response.addCookie(
        rememberMe
            ? jwtTokenProvider.generateRememberMeCookie(authentication)
            : jwtTokenProvider.generateSessionCookie(authentication));
    httpUtils.writeJsonToResponse(response, userService.getCurrentUser());
  }
}
