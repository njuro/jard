package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.controllers.validation.FormValidationException;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.forms.RegisterForm;
import com.github.njuro.jboard.services.UserService;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserFacade {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserFacade(final PasswordEncoder passwordEncoder, final UserService userService) {
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  public User registerUser(@NotNull final RegisterForm registerForm) {
    if (userService.doesUserExists(registerForm.getUsername())) {
      throw new FormValidationException("User with this name already exists");
    }

    if (userService.doesEmailExists(registerForm.getEmail())) {
      throw new FormValidationException("User with this e-mail already exists");
    }

    final User user = registerForm.toUser();
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userService.saveUser(user);
  }

  public static User getCurrentUser() {
    return UserService.getCurrentUser();
  }

  public User updateUser(final User user) {
    return userService.saveUser(user);
  }

  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }
}
