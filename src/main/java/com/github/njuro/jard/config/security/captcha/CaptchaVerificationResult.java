package com.github.njuro.jard.config.security.captcha;

import java.util.List;

/**
 * Result of verification of CAPTCHA token.
 *
 * @see CaptchaProvider
 */
public interface CaptchaVerificationResult {

  /** @return true if CAPTCHA token was successfully verified, false otherwise */
  boolean isVerified();

  /** @return list of errors in case verification of token failed */
  List<String> getErrors();
}
