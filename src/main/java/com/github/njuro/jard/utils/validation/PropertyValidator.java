package com.github.njuro.jard.utils.validation;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class PropertyValidator {

  private final LocalValidatorFactoryBean validator;

  @Autowired
  public PropertyValidator(LocalValidatorFactoryBean validator) {
    this.validator = validator;
  }

  /**
   * Validates JSR 303 annotations on given object.
   *
   * @param target object to validate
   * @throws FormValidationException if validation fails
   */
  public void validate(Object target) {
    BindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
    validator.validate(target, bindingResult);
    if (bindingResult.hasFieldErrors()) {
      throw new FormValidationException(bindingResult);
    }
  }

  /**
   * Validates JSR 303 annotations on given property.
   *
   * @param target object which contains property to be validated
   * @param property name of the property to be validated
   * @throws FormValidationException if validation fails
   */
  public <T> void validateProperty(T target, String property) {
    Set<ConstraintViolation<T>> violations = validator.validateProperty(target, property);
    if (!violations.isEmpty()) {
      String errors =
          violations.stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.joining(", "));
      throw new FormValidationException(
          String.format("Validation of property %s failed: %s", property, errors));
    }
  }
}
