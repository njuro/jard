package com.github.njuro.jboard.controllers.rest.validation;


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
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new ValidationErrors(ex.getBindingResult()), headers, status);
    }

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<Object> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(new ValidationErrors(ex.getBindingResult()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
