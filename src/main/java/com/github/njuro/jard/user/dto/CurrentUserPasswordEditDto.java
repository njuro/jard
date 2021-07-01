package com.github.njuro.jard.user.dto;

import static com.github.njuro.jard.common.InputConstraints.MIN_PASSWORD_LENGTH;

import com.github.njuro.jard.user.User;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for changing current user's password */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("JavadocReference")
public class CurrentUserPasswordEditDto {

  /** {@link User#password} */
  @NotNull private String currentPassword;

  /** {@link User#password} */
  @Size(min = MIN_PASSWORD_LENGTH, message = "{validation.user.password.length}")
  private String newPassword;

  /** {@link User#password} */
  private String newPasswordRepeated;

  /** Validates that {@link #newPassword} and {@link #newPasswordRepeated} are equal. */
  @AssertTrue(message = "{validation.user.password.match}")
  public boolean isPasswordMatching() {
    return newPassword != null && newPassword.equals(newPasswordRepeated);
  }
}
