package com.github.njuro.jboard.config.security;

import com.github.njuro.jboard.config.security.jwt.JwtTokenProvider;
import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom login success handler, which intercepts the request to update last login time and IP of {@link User} who
 * just authenticated, logs the event and redirects user to authenticated section.
 *
 * @author njuro
 * @see SecurityConfig
 */
@Component
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public LoginSuccessHandler(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        setRedirectStrategy(new NoRedirectStrategy());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
        logUserLogin(authentication, request.getRemoteAddr());
        response.addCookie(getJwtCookie(authentication));
    }

    private void logUserLogin(Authentication authentication, String ipAddress) {
        User user = (User) authentication.getPrincipal();
        user.setLastLoginIp(ipAddress);
        user.setLastLogin(LocalDateTime.now());
        userService.saveUser(user);
        log.debug("User {} logged from IP {}", user.getUsername(), user.getLastLoginIp());
    }

    private Cookie getJwtCookie(Authentication authentication) {
        String token = jwtTokenProvider.generateToken(authentication);
        Cookie cookie = new Cookie(Constants.JWT_COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    protected static class NoRedirectStrategy implements RedirectStrategy {
        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
            // no redirect
        }
    }
}
