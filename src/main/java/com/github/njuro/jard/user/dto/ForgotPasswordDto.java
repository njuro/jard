package com.github.njuro.jard.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordDto {

  private String username;

  private String ip;

  private String userAgent;
}
