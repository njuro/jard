package com.github.njuro.jard.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.RedirectStrategy;

/** No-op redirect strategy. */
public class NoRedirectStrategy implements RedirectStrategy {

  @Override
  public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
    // no redirect
  }
}
