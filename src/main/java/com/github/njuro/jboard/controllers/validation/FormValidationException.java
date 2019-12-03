package com.github.njuro.jboard.controllers.validation;

import javax.validation.ValidationException;
import lombok.Getter;
import org.springframework.validation.BindingResult;

public class FormValidationException extends ValidationException {

  private static final long serialVersionUID = 8579539307467463861L;
  @Getter private BindingResult bindingResult;

  public FormValidationException(final BindingResult bindingResult) {
    this.bindingResult = bindingResult;
  }

  public FormValidationException(final String message) {
    super(message);
  }
}
