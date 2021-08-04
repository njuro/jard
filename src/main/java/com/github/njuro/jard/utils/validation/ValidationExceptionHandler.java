package com.github.njuro.jard.utils.validation;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for validation exceptions.
 *
 * @see PropertyValidationException
 */
@RestControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

  @NotNull
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NotNull HttpHeaders headers,
      @NotNull HttpStatus status,
      @NotNull WebRequest request) {
    return new ResponseEntity<>(new ValidationErrors(ex.getBindingResult()), headers, status);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(PropertyValidationException.class)
  private ValidationErrors handleValidationException(PropertyValidationException ex) {
    return ex.getBindingResult() != null
        ? new ValidationErrors(ex.getBindingResult())
        : new ValidationErrors(ex.getMessage());
  }
}
