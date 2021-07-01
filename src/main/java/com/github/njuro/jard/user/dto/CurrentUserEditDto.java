package com.github.njuro.jard.user.dto;

import com.github.njuro.jard.user.User;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for editing of current user information. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("JavadocReference")
public class CurrentUserEditDto {

  /** {@link User#email } */
  @Email private String email;
}
