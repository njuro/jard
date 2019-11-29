package com.github.njuro.jboard.controllers.validation;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import javax.validation.ValidationException;

public class FormValidationException extends ValidationException {

    @Getter
    private BindingResult bindingResult;

    public FormValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public FormValidationException(String message) {
        super(message);
    }
}
