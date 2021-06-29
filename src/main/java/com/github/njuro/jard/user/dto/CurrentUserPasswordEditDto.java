package com.github.njuro.jard.user.dto;

import static com.github.njuro.jard.common.InputConstraints.MIN_PASSWORD_LENGTH;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/** DTO for changing current user's password */
@Data
@Builder
public class CurrentUserPasswordEditDto {

  @NotNull private String currentPassword;

  @Size(min = MIN_PASSWORD_LENGTH, message = "{validation.user.password.length}")
  private String newPassword;

  private String newPasswordRepeated;

  /** Validates that {@link #newPassword} and {@link #newPasswordRepeated} are equal. */
  @AssertTrue(message = "{validation.user.password.match}")
  public boolean isPasswordMatching() {
    return newPassword != null && newPassword.equals(newPasswordRepeated);
  }
}
