package com.github.njuro.jboard.user;

import static com.github.njuro.jboard.common.Constants.IP_PATTERN;
import static com.github.njuro.jboard.common.Constants.MAX_USERNAME_LENGTH;
import static com.github.njuro.jboard.common.Constants.MIN_PASSWORD_LENGTH;
import static com.github.njuro.jboard.common.Constants.MIN_USERNAME_LENGTH;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** Data transfer object for "create new user" form */
@Data
@Builder
public class UserForm {

  @Size(
      min = MIN_USERNAME_LENGTH,
      max = MAX_USERNAME_LENGTH,
      message = "{validation.user.username.length}")
  private String username;

  @Size(min = MIN_PASSWORD_LENGTH, message = "{validation.user.password.length}")
  private String password;

  private String passwordRepeated;

  @Email(message = "{validation.user.email.invalid}")
  private String email;

  @Pattern(regexp = IP_PATTERN)
  private String registrationIp;

  @NotNull(message = "{validation.user.role.null}")
  private UserRole role;

  @AssertTrue(message = "{validation.user.password.match}")
  public boolean isPasswordMatching() {
    return password.equals(passwordRepeated);
  }

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
