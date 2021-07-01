package com.github.njuro.jard.user.dto;

import static com.github.njuro.jard.common.InputConstraints.MIN_PASSWORD_LENGTH;

import com.github.njuro.jard.user.User;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** DTO for reseting user's password. */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class ResetPasswordDto {

  /** Secret token (obtained by user via mail) */
  private String token;

  /** {@link User#password} */
  @Size(min = MIN_PASSWORD_LENGTH, message = "{validation.user.password.length}")
  private String password;

  /** {@link User#password} */
  private String passwordRepeated;

  /** Validates that {@link #password} and {@link #passwordRepeated} are equal. */
  @AssertTrue(message = "{validation.user.password.match}")
  public boolean isPasswordMatching() {
    return password != null && password.equals(passwordRepeated);
  }
}
