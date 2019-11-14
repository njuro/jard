package com.github.njuro.jboard.controllers.rest.validation;

import lombok.Getter;
import org.springframework.validation.BindingResult;

public class ValidationException extends Exception {

    @Getter
    private BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
