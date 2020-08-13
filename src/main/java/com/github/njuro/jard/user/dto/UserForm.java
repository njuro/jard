package com.github.njuro.jard.user.dto;

import static com.github.njuro.jard.common.Constants.IP_PATTERN;
import static com.github.njuro.jard.common.InputConstraints.*;

import com.github.njuro.jard.user.User;
import com.github.njuro.jard.user.UserRole;
import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

/** Form for creating/updating a {@link User} */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class UserForm {

  /** {@link User#username} */
  @Size(
      min = MIN_USERNAME_LENGTH,
      max = MAX_USERNAME_LENGTH,
      message = "{validation.user.username.length}")
  private String username;

  /** {@link User#password} */
  @Size(min = MIN_PASSWORD_LENGTH, message = "{validation.user.password.length}")
  private String password;

  /** {@link User#password} */
  private String passwordRepeated;

  /** {@link User#email} */
  @Email(message = "{validation.user.email.invalid}")
  private String email;

  /** {@link User#registrationIp} */
  @Pattern(regexp = IP_PATTERN)
  private String registrationIp;

  /** {@link User#role} */
  @NotNull(message = "{validation.user.role.null}")
  private UserRole role;

  /** Validates that {@link #password} and {@link #passwordRepeated} are equal. */
  @AssertTrue(message = "{validation.user.password.match}")
  public boolean isPasswordMatching() {
    return password != null && password.equals(passwordRepeated);
  }

  /** Creates {@link User} from values of this form and marks him/her as enabled. */
  public User toUser() {
    return User.builder()
        .username(username)
        .password(password)
        .email(email)
        .registrationIp(registrationIp)
        .role(role)
        .authorities(role.getDefaultAuthorites())
        .enabled(true)
        .build();
  }
}
