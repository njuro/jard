package com.github.njuro.jard.user;

import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.user.dto.UserForm;
import com.github.njuro.jard.utils.validation.FormValidationException;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserFacade extends BaseFacade<User, UserDto> implements UserDetailsService {

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
  public UserDto createUser(@NotNull UserForm userForm) {
    if (userService.doesUserExists(userForm.getUsername())) {
      throw new FormValidationException("User with this name already exists");
    }

    if (userService.doesEmailExists(userForm.getEmail())) {
      throw new FormValidationException("User with this e-mail already exists");
    }

    User user = userForm.toUser();
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return toDto(userService.saveUser(user));
  }

  /** {@link UserService#resolveUser(String)} */
  public UserDto resolveUser(String username) {
    return toDto(userService.resolveUser(username));
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = userService.resolveUser(username);
    if (user == null) {
      throw new UsernameNotFoundException("User " + username + " was not found");
    }

    return user;
  }

  /** {@link UserService#getAllUsers()} */
  public List<UserDto> getAllUsers() {
    return toDtoList(userService.getAllUsers());
  }

  /** {@link UserService#getCurrentUser()} */
  public UserDto getCurrentUser() {
    return toDto(userService.getCurrentUser());
  }

  /** {@link UserService#hasCurrentUserAuthority(UserAuthority)} */
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
  public UserDto editUser(UserDto oldUser, UserForm updatedUser) {
    User oldUserEntity = toEntity(oldUser);
    oldUserEntity.setEmail(updatedUser.getEmail());
    oldUserEntity.setRole(updatedUser.getRole());
    oldUserEntity.setAuthorities(updatedUser.getRole().getDefaultAuthorites());
    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
      if (updatedUser.isPasswordMatching()) {
        oldUserEntity.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
      } else {
        // TODO automatic validation / no hardcoded message
        throw new FormValidationException("Passwords do not match");
      }
    }

    return toDto(userService.saveUser(oldUserEntity));
  }

  /** {@link UserService#deleteUser(User)} */
  public void deleteUser(UserDto user) {
    userService.deleteUser(toEntity(user));
  }
}
