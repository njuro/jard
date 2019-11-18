package com.github.njuro.jboard.controllers.rest;

import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody @Valid RegisterForm registerForm, HttpServletRequest request) {
        registerForm.setRegistrationIp(request.getRemoteAddr());
        return userService.createUser(registerForm);
    }
}
