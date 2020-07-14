package com.github.njuro.jard.user;

import com.github.njuro.jard.utils.validation.FormValidationException;
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

  /**
   * Creates and save new user. Password of the user is encoded by {@link #passwordEncoder} before
   * storing in database.
   *
   * @param userForm form with user data
   * @return created user
   * @throws FormValidationException if user with such name or e-mail already exists
   */
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

  /** @see UserService#resolveUser(String) */
  public User resolveUser(String username) {
    return userService.resolveUser(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = resolveUser(username);
    if (user == null) {
      throw new UsernameNotFoundException("User " + username + " was not found");
    }

    return user;
  }

  /** @see UserService#getAllUsers() */
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  /** @see UserService#getCurrentUser() */
  public User getCurrentUser() {
    return userService.getCurrentUser();
  }

  /** @see UserService#hasCurrentUserAuthority(UserAuthority) */
  public boolean hasCurrentUserAuthority(UserAuthority authority) {
    return userService.hasCurrentUserAuthority(authority);
  }

  /**
   * Edits an user. Only e-mail, active role (and therefore default authorities) can be edited.
   *
   * @param oldUser user to be edited
   * @param updatedUser form with new values
   * @return edited user
   */
  public User editUser(User oldUser, UserForm updatedUser) {
    oldUser.setEmail(updatedUser.getEmail());
    oldUser.setRole(updatedUser.getRole());
    oldUser.setAuthorities(updatedUser.getRole().getDefaultAuthorites());
    return userService.saveUser(oldUser);
  }

  /** @see UserService#deleteUser(User) */
  public void deleteUser(User user) {
    userService.deleteUser(user);
  }
}
