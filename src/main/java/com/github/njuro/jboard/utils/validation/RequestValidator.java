package com.github.njuro.jboard.utils.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class RequestValidator {

  private final LocalValidatorFactoryBean validator;

  @Autowired
  public RequestValidator(LocalValidatorFactoryBean validator) {
    this.validator = validator;
  }

  public void validate(Object target) {
    BindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
    validator.validate(target, bindingResult);
    if (bindingResult.hasFieldErrors()) {
      throw new FormValidationException(bindingResult);
    }
  }
}
