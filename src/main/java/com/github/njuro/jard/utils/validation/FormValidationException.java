package com.github.njuro.jard.utils.validation;

import javax.validation.ValidationException;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * Exception to be thrown when business validation of request object (such as form) fails. Handled
 * by {@link ValidationExceptionHandler}.
 */
public class FormValidationException extends ValidationException {

  private static final long serialVersionUID = 8579539307467463861L;
  @Getter private final BindingResult bindingResult;

  public FormValidationException(BindingResult bindingResult) {
    this.bindingResult = bindingResult;
  }

  public FormValidationException(String message) {
    super(message);
    bindingResult = null;
  }
}
