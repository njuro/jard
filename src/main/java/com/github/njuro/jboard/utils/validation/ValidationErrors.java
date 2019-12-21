package com.github.njuro.jboard.utils.validation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Data
public class ValidationErrors {

  public static final String OBJECT_ERROR = "object";

  private LocalDateTime timestamp;
  private Map<String, String> errors;

  public ValidationErrors() {
    timestamp = LocalDateTime.now();
    errors = new HashMap<>();
  }

  public ValidationErrors(BindingResult validationResult) {
    this();
    //noinspection ConstantConditions
    errors =
        validationResult.getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
  }

  public ValidationErrors(String objectError) {
    this();
    errors.put(OBJECT_ERROR, objectError);
  }
}
