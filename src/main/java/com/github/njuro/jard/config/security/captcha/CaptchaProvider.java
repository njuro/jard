package com.github.njuro.jard.config.security.captcha;

/** Provider for verifying CAPTCHA response token. */
public interface CaptchaProvider {

  CaptchaVerificationResult verifyCaptchaToken(String captchaToken);
}
