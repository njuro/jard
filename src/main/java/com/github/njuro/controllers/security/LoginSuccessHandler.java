package com.github.njuro.controllers.security;

import com.github.njuro.models.User;
import com.github.njuro.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Log
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    @Autowired
    public LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        user.setLastLoginIp(request.getRemoteAddr());
        user.setLastLogin(LocalDateTime.now());
        userService.saveUser(user);

        log.info("User " + user.getUsername() + " logged successfully");

        setDefaultTargetUrl("/auth");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
