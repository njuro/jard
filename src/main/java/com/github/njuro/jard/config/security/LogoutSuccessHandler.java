package com.github.njuro.jard.config.security;

import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  public LogoutSuccessHandler() {
    setRedirectStrategy(new NoRedirectStrategy());
  }
}
