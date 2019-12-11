package com.github.njuro.jboard.utils.validation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

@Data
public class ValidationErrors {

  private LocalDateTime timestamp;
  private List<String> errors;

  public ValidationErrors() {
    timestamp = LocalDateTime.now();
    errors = new ArrayList<>();
  }

  public ValidationErrors(BindingResult validationResult) {
    this();
    errors =
        validationResult.getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());
  }

  public ValidationErrors(String... errors) {
    this();
    this.errors.addAll(Arrays.asList(errors));
  }
}
