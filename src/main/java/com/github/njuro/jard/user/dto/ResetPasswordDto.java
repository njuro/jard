package com.github.njuro.jard.user.dto;

import static com.github.njuro.jard.common.InputConstraints.MIN_PASSWORD_LENGTH;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordDto {

  private String username;

  private String token;

  @Size(min = MIN_PASSWORD_LENGTH, message = "{validation.user.password.length}")
  private String password;

  private String passwordRepeated;

  /** Validates that {@link #password} and {@link #passwordRepeated} are equal. */
  @AssertTrue(message = "{validation.user.password.match}")
  public boolean isPasswordMatching() {
    return password != null && password.equals(passwordRepeated);
  }
}
