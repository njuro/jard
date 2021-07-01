package com.github.njuro.jard.user;

import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.config.security.captcha.CaptchaProvider;
import com.github.njuro.jard.config.security.captcha.CaptchaVerificationResult;
import com.github.njuro.jard.user.dto.*;
import com.github.njuro.jard.user.token.UserToken;
import com.github.njuro.jard.user.token.UserTokenService;
import com.github.njuro.jard.user.token.UserTokenType;
import com.github.njuro.jard.utils.EmailService;
import com.github.njuro.jard.utils.validation.FormValidationException;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserFacade extends BaseFacade<User, UserDto> implements UserDetailsService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final UserTokenService userTokenService;
  private final EmailService emailService;
  private final CaptchaProvider captchaProvider;

  @Value("${client.base.url:localhost}")
  private String clientBaseUrl;

  @Autowired
  public UserFacade(
      @Lazy PasswordEncoder passwordEncoder,
      UserService userService,
      UserTokenService userTokenService,
      EmailService emailService,
      CaptchaProvider captchaProvider) {
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.userTokenService = userTokenService;
    this.emailService = emailService;
    this.captchaProvider = captchaProvider;
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
        throw new FormValidationException("Passwords do not match");
      }
    }

    return toDto(userService.saveUser(oldUserEntity));
  }

  /**
   * Edits information of current user.
   *
   * @param userChange object with updated information.
   * @throws FormValidationException when no user is logged in or updated email is already in use
   * @return updated user
   */
  public UserDto editCurrentUser(CurrentUserEditDto userChange) {
    var currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      throw new FormValidationException("No user is authenticated");
    }

    if (userChange.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
      return toDto(currentUser);
    }

    if (userService.doesEmailExists(userChange.getEmail())) {
      throw new FormValidationException("E-mail already in use by different user");
    }

    currentUser.setEmail(userChange.getEmail());
    return toDto(userService.saveUser(currentUser));
  }

  /**
   * Edits password of current user.
   *
   * @param passwordChange object with new password
   * @throws FormValidationException when no user is logged in or given current password is
   *     incorrect
   */
  public void editCurrentUserPassword(CurrentUserPasswordEditDto passwordChange) {
    var currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      throw new FormValidationException("No user is authenticated");
    }
    if (!passwordEncoder.matches(passwordChange.getCurrentPassword(), currentUser.getPassword())) {
      throw new FormValidationException("Incorrect current password");
    }

    currentUser.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
    userService.saveUser(currentUser);
  }

  public void sendPasswordRecoveryLink(ForgotPasswordDto forgotRequest) {
    log.info("Password recovery link requested by user {}", forgotRequest.getUsername());
    verifyCaptcha(forgotRequest.getCaptchaToken());

    User user;
    try {
      user = userService.resolveUser(forgotRequest.getUsername());
    } catch (UserNotFoundException ex) {
      log.error("User {} does not exists, not sending recovery link.", forgotRequest.getUsername());
      return;
    }

    if (userTokenService.doesTokenForUserExists(user, UserTokenType.PASSWORD_RECOVERY)) {
      log.info("User {} already has valid password recovery token", user.getUsername());
      return;
    }

    if (user.getEmail() == null) {
      log.error("User {} does not have e-mail address set", user.getUsername());
      return;
    }

    UserToken token = userTokenService.generateToken(user, UserTokenType.PASSWORD_RECOVERY);

    // TODO use template
    emailService.sendMail(
        user.getEmail(),
        "Recovery password link",
        String.format(
            "Hey %s, here is your recovery password link: %s/password-recovery?token=%s (request from IP %s with user agent %s)",
            user.getUsername(),
            clientBaseUrl,
            token.getValue(),
            forgotRequest.getIp(),
            forgotRequest.getUserAgent()));
  }

  public void resetPassword(ResetPasswordDto resetRequest) {
    var token = userTokenService.resolveToken(resetRequest.getToken());

    if (token == null || token.getType() != UserTokenType.PASSWORD_RECOVERY) {
      throw new FormValidationException("Invalid token");
    }

    var user = token.getUser();
    log.info("Resetting password of user {}", user.getUsername());
    user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
    userService.saveUser(user);
    userTokenService.deleteToken(user, UserTokenType.PASSWORD_RECOVERY);
    emailService.sendMail(
        user.getEmail(),
        "Your password was changed",
        String.format("Hey %s, your password was changed", user.getUsername()));
  }

  /** {@link UserService#deleteUser(User)} */
  public void deleteUser(UserDto user) {
    userService.deleteUser(toEntity(user));
  }

  /**
   * Verifies CAPTCHA response token.
   *
   * @param captchaToken CAPTCHA response token to verify
   * @throws FormValidationException if verification of token failed
   */
  private void verifyCaptcha(String captchaToken) {
    CaptchaVerificationResult result = captchaProvider.verifyCaptchaToken(captchaToken);
    if (!result.isVerified()) {
      log.warn(
          String.format(
              "Captcha verification failed: [%s]", String.join(", ", result.getErrors())));
      throw new FormValidationException("Failed to verify captcha");
    }
  }
}
