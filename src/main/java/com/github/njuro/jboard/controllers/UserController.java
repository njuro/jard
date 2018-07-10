package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for creating new users and logging in
 *
 * @njuro
 */
@Controller
@Slf4j
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Shows form for creating new user
     */
    @GetMapping("/auth/register")
    public String showRegisterForm() {
        return "register";
    }

    /**
     * Attempts to create new user
     */
    @PostMapping("/auth/register")
    public String registerUser(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                               BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (userService.doesUserExists(registerForm.getUsername()))
            result.rejectValue("username", "username.exists", "Username is already registered");
        if (userService.doesEmailExists(registerForm.getEmail())) {
            result.rejectValue("email", "email.exists", "E-mail is already registered");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerForm", result);
            redirectAttributes.addFlashAttribute("registerForm", registerForm);

            log.debug("Creating user failed: {}", result.getAllErrors());
            return "redirect:/auth/register";
        }

        User user = userService.createUser(registerForm, passwordEncoder);
        user.setRegistrationIp(request.getRemoteAddr());
        userService.saveUser(user);

        log.debug("Created user {}", user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


}
