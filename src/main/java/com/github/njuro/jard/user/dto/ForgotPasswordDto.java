package com.github.njuro.jard.user.dto;

import com.github.njuro.jard.user.User;
import lombok.Builder;
import lombok.Data;

/** DTO for requesting a trigger of password reset process (in case of forgotten password). */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class ForgotPasswordDto {

  /** {@link User#username} */
  private String username;

  /** IP the reset request came from. */
  private String ip;

  /** User-Agent header of reset request. */
  private String userAgent;

  /** Captcha token for bot protection. */
  private String captchaToken;
}
