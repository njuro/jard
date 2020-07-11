package com.github.njuro.jard.utils.validation;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/** Class for encapsulating validation errors and sending them to client. */
@Data
public class ValidationErrors {

  /**
   * Special "field name" to be used if validation fails on the object itself (i.e. poster's IP is
   * banned).
   */
  public static final String OBJECT_ERROR = "object";

  /** When the error occurred. */
  private OffsetDateTime timestamp;

  /** Map of the errors - key is name of invalid field, value is the field value. */
  private Map<String, String> errors;

  public ValidationErrors() {
    timestamp = OffsetDateTime.now();
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
