package com.github.njuro.jboard.controllers.validation;

import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ValidationErrors {

    private LocalDateTime timestamp;
    private List<String> errors;

    public ValidationErrors() {
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }

    public ValidationErrors(BindingResult validationResult) {
        this();
        this.errors = validationResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
    }

    public ValidationErrors(String... errors) {
        this();
        this.errors.addAll(Arrays.asList(errors));
    }
}
