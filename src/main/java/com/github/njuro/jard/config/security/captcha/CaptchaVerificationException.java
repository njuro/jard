package com.github.njuro.jard.config.security.captcha;

import com.github.njuro.jard.utils.validation.PropertyValidationException;
import com.github.njuro.jard.utils.validation.ValidationExceptionHandler;
import java.io.Serial;

/**
 * Exception to be thrown when captcha verification fails. Handled by {@link
 * ValidationExceptionHandler}.
 *
 * <p>Note: This exception isn't thrown by {@link CaptchaProvider} itself, but on appropriate places
 * in business logic, where verification failure needs to be propagated to client.
 */
public class CaptchaVerificationException extends PropertyValidationException {

  @Serial private static final long serialVersionUID = 3101215952448563829L;

  public CaptchaVerificationException() {
    super("Captcha verification failed");
  }
}
