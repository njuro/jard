package com.github.njuro.jboard.controllers.rest.validation;

import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ValidationErrors {

    private LocalDateTime timestamp;
    private List<String> errors;

    public ValidationErrors(BindingResult validationResult) {
        this.errors = validationResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        this.timestamp = LocalDateTime.now();
    }
}
