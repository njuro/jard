package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.CurrentUser;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/current")
    public CurrentUser getCurrentUser() {
        return userService.getCurrentUserReduced();
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody @Valid RegisterForm registerForm, HttpServletRequest request) {
        registerForm.setRegistrationIp(request.getRemoteAddr());
        return userService.createUser(registerForm);
    }
}
