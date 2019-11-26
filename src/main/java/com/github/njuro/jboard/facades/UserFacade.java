package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class UserFacade {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserFacade(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public User registerUser(@NotNull RegisterForm registerForm) {
        User user = registerForm.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userService.saveUser(user);
    }

    public User getCurrentUser() {
        return UserService.getCurrentUser();
    }

    public User updateUser(User user) {
        return userService.saveUser(user);
    }
}
