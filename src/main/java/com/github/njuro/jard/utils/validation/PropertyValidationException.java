package com.github.njuro.jard.utils.validation;

import java.io.Serial;
import javax.validation.ValidationException;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * Exception to be thrown when business validation of request object (such as form) fails. Handled
 * by {@link ValidationExceptionHandler}.
 */
public class PropertyValidationException extends ValidationException {

  @Serial private static final long serialVersionUID = 8579539307467463861L;

  @Getter private final transient BindingResult bindingResult;

  public PropertyValidationException(BindingResult bindingResult) {
    this.bindingResult = bindingResult;
  }

  public PropertyValidationException(String message) {
    super(message);
    bindingResult = null;
  }
}
