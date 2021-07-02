package com.github.njuro.jard.user;

import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.config.security.captcha.CaptchaProvider;
import com.github.njuro.jard.config.security.captcha.CaptchaVerificationResult;
import com.github.njuro.jard.user.dto.*;
import com.github.njuro.jard.user.token.UserToken;
import com.github.njuro.jard.user.token.UserTokenService;
import com.github.njuro.jard.user.token.UserTokenType;
import com.github.njuro.jard.utils.EmailService;
import com.github.njuro.jard.utils.TemplateService;
import com.github.njuro.jard.utils.validation.PropertyValidationException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  private final TemplateService templateService;
  private final CaptchaProvider captchaProvider;

  @Value("${client.base.url:localhost}")
  private String clientBaseUrl;

  @Autowired
  public UserFacade(
      @Lazy PasswordEncoder passwordEncoder,
      UserService userService,
      UserTokenService userTokenService,
      @Lazy EmailService emailService,
      TemplateService templateService,
      CaptchaProvider captchaProvider) {
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.userTokenService = userTokenService;
    this.emailService = emailService;
    this.templateService = templateService;
    this.captchaProvider = captchaProvider;
  }

  /**
   * Creates and save new user. Password of the user is encoded by {@link #passwordEncoder} before
   * storing in database.
   *
   * @param userForm form with user data
   * @return created user
   * @throws PropertyValidationException if user with such name or e-mail already exists
   */
  public UserDto createUser(@NotNull UserForm userForm) {
    if (userService.doesUserExists(userForm.getUsername())) {
      throw new PropertyValidationException("User with this name already exists");
    }

    if (userService.doesEmailExists(userForm.getEmail())) {
      throw new PropertyValidationException("User with this e-mail already exists");
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
        throw new PropertyValidationException("Passwords do not match");
      }
    }

    return toDto(userService.saveUser(oldUserEntity));
  }

  /**
   * Edits information of current user.
   *
   * @param userChange object with updated information.
   * @throws PropertyValidationException when no user is logged in or updated email is already in
   *     use
   * @return updated user
   */
  public UserDto editCurrentUser(CurrentUserEditDto userChange) {
    var currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      throw new PropertyValidationException("No user is authenticated");
    }

    if (userChange.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
      return toDto(currentUser);
    }

    if (userService.doesEmailExists(userChange.getEmail())) {
      throw new PropertyValidationException("E-mail already in use by different user");
    }

    currentUser.setEmail(userChange.getEmail());
    return toDto(userService.saveUser(currentUser));
  }

  /**
   * Edits password of current user.
   *
   * @param passwordChange object with new password
   * @throws PropertyValidationException when no user is logged in or given current password is
   *     incorrect
   */
  public void editCurrentUserPassword(CurrentUserPasswordEditDto passwordChange) {
    var currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      throw new PropertyValidationException("No user is authenticated");
    }
    if (!passwordEncoder.matches(passwordChange.getCurrentPassword(), currentUser.getPassword())) {
      throw new PropertyValidationException("Incorrect current password");
    }

    currentUser.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
    userService.saveUser(currentUser);
  }

  /**
   * Sends e-mail with link for password reset to given user.
   *
   * @param forgotRequest - metadata about reset request
   * @throws PropertyValidationException if provided captcha token is invalid, user is not found,
   *     user has already valid reset token, user doesn't have e-mail set or sending an e-mail
   *     failed.
   */
  public void sendPasswordResetLink(ForgotPasswordDto forgotRequest) {
    log.info("Password reset link requested by user {}", forgotRequest.getUsername());
    verifyCaptcha(forgotRequest.getCaptchaToken());

    User user = userService.resolveUser(forgotRequest.getUsername());
    if (userTokenService.doesTokenForUserExists(user, UserTokenType.PASSWORD_RESET)) {
      throw new PropertyValidationException(
          String.format(
              "User %s has already valid password reset token issued", user.getUsername()));
    }

    if (user.getEmail() == null) {
      throw new PropertyValidationException(
          String.format("User %s does not have e-mail address set", user.getUsername()));
    }

    UserToken token = userTokenService.generateToken(user, UserTokenType.PASSWORD_RESET);

    var message =
        templateService.resolveTemplate(
            "forgot_password",
            Map.of(
                "username",
                user.getUsername(),
                "clientUrl",
                clientBaseUrl,
                "token",
                token.getValue(),
                "ip",
                forgotRequest.getIp(),
                "userAgent",
                Optional.ofNullable(forgotRequest.getUserAgent()).orElse("Unknown"),
                "timestamp",
                OffsetDateTime.now()));
    emailService.sendMail(user.getEmail(), "Reset your password", message);
  }

  /**
   * Resets password for user.
   *
   * @param resetRequest - metadata about reset request
   * @throws PropertyValidationException if provided token is invalid or sending the mail failed.
   */
  public void resetPassword(ResetPasswordDto resetRequest) {
    var token =
        userTokenService.resolveToken(resetRequest.getToken(), UserTokenType.PASSWORD_RESET);
    if (token == null) {
      throw new PropertyValidationException("Invalid token");
    }

    var user = token.getUser();
    log.info("Resetting password of user {}", user.getUsername());
    user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
    userService.saveUser(user);
    userTokenService.deleteToken(user, UserTokenType.PASSWORD_RESET);

    var message =
        templateService.resolveTemplate(
            "reset_password",
            Map.of(
                "username",
                user.getUsername(),
                "clientUrl",
                clientBaseUrl,
                "timestamp",
                OffsetDateTime.now()));
    emailService.sendMail(user.getEmail(), "Your password has been updated", message);
  }

  /** {@link UserService#deleteUser(User)} */
  public void deleteUser(UserDto user) {
    userService.deleteUser(toEntity(user));
  }

  /**
   * Verifies CAPTCHA response token.
   *
   * @param captchaToken CAPTCHA response token to verify
   * @throws PropertyValidationException if verification of token failed
   */
  private void verifyCaptcha(String captchaToken) {
    CaptchaVerificationResult result = captchaProvider.verifyCaptchaToken(captchaToken);
    if (!result.isVerified()) {
      log.warn(
          String.format(
              "Captcha verification failed: [%s]", String.join(", ", result.getErrors())));
      throw new PropertyValidationException("Failed to verify captcha");
    }
  }
}
