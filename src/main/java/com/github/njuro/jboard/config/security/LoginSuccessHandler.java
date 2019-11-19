package com.github.njuro.jboard.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jboard.config.security.jwt.JwtTokenProvider;
import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
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

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public LoginSuccessHandler(ObjectMapper objectMapper, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        setRedirectStrategy(new NoRedirectStrategy());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        user.setLastLoginIp(request.getRemoteAddr());
        user.setLastLogin(LocalDateTime.now());
        userService.saveUser(user);
        log.debug("User {} logged from IP {}", user.getUsername(), user.getLastLoginIp());

        super.onAuthenticationSuccess(request, response, authentication);

        boolean rememberMe = Boolean.parseBoolean(request.getAttribute(Constants.JWT_REMEMBER_ME_ATTRIBUTE).toString());
        response.addCookie(rememberMe ? jwtTokenProvider.generateRememberMeCookie(authentication) : jwtTokenProvider.generateSessionCookie(authentication));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(userService.getCurrentUserReduced()));
        response.getWriter().flush();
    }
}
