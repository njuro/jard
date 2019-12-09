package com.github.njuro.jboard.controllers.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@Component
public class RequestValidator {

  private final Validator validator;

  @Autowired
  public RequestValidator(@Qualifier("defaultValidator") final Validator validator) {
    this.validator = validator;
  }

  public void validate(final Object target) {
    final BindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
    validator.validate(target, bindingResult);
    if (bindingResult.hasFieldErrors()) {
      throw new FormValidationException(bindingResult);
    }
  }
}
