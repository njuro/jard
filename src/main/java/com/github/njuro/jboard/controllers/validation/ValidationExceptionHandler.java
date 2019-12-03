package com.github.njuro.jboard.controllers.validation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return new ResponseEntity<>(new ValidationErrors(ex.getBindingResult()), headers, status);
  }

  @ExceptionHandler(FormValidationException.class)
  private ResponseEntity<Object> handleValidationException(FormValidationException ex) {
    ValidationErrors errors =
        ex.getBindingResult() != null
            ? new ValidationErrors(ex.getBindingResult())
            : new ValidationErrors(ex.getMessage());
    return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}
