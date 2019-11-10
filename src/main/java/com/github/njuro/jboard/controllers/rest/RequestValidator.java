package com.github.njuro.jboard.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@Component
public class RequestValidator {

    private final Validator validator;

    @Autowired
    public RequestValidator(@Qualifier("defaultValidator") Validator validator) {
        this.validator = validator;
    }

    public BindingResult validate(Object target, String targetName) {
        BindingResult bindingResult = new BeanPropertyBindingResult(target, targetName);
        validator.validate(target, bindingResult);
        return bindingResult;
    }
}
