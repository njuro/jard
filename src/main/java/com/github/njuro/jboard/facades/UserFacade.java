package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.controllers.validation.FormValidationException;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.forms.UserForm;
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
  public UserFacade(PasswordEncoder passwordEncoder, UserService userService) {
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  public User createUser(@NotNull UserForm userForm) {
    if (userService.doesUserExists(userForm.getUsername())) {
      throw new FormValidationException("User with this name already exists");
    }

    if (userService.doesEmailExists(userForm.getEmail())) {
      throw new FormValidationException("User with this e-mail already exists");
    }

    User user = userForm.toUser();
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userService.saveUser(user);
  }

  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  public User getCurrentUser() {
    return userService.getCurrentUser();
  }

  public User updateUser(User user) {
    return userService.saveUser(user);
  }
}
