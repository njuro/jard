package com.github.njuro.jboard.user;

import com.github.njuro.jboard.utils.validation.FormValidationException;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserFacade implements UserDetailsService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserFacade(@Lazy PasswordEncoder passwordEncoder, UserService userService) {
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

  public User resolveUser(String username) {
    return userService.resolveUser(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = resolveUser(username);
    if (user == null) {
      throw new UsernameNotFoundException("User " + username + " was not found");
    }

    return user;
  }

  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  public User getCurrentUser() {
    return userService.getCurrentUser();
  }

  public boolean hasCurrentUserAuthority(UserAuthority authority) {
    return userService.hasCurrentUserAuthority(authority);
  }

  public User editUser(User oldUser, UserForm updatedUser) {
    oldUser.setEmail(updatedUser.getEmail());
    oldUser.setRole(updatedUser.getRole());
    oldUser.setAuthorities(updatedUser.getRole().getDefaultAuthorites());
    return userService.updateUser(oldUser);
  }

  public void deleteUser(User user) {
    userService.deleteUser(user);
  }
}
