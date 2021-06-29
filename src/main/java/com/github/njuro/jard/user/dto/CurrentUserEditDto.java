package com.github.njuro.jard.user.dto;

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
public class CurrentUserEditDto {

  @Email private String email;
}
