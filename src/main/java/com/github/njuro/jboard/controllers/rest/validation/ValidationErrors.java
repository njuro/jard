package com.github.njuro.jboard.controllers.rest.validation;

import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ValidationErrors {

    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public ValidationErrors(BindingResult validationResult) {
        //noinspection ConstantConditions
        this.errors = validationResult.getFieldErrors().stream().collect(
                Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
        this.timestamp = LocalDateTime.now();
    }
}
