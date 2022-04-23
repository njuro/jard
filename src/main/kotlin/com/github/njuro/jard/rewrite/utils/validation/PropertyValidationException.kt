package com.github.njuro.jard.rewrite.utils.validation

import org.springframework.validation.BindingResult
import javax.validation.ValidationException

/**
 * Exception to be thrown when business validation of request object (such as form) fails. Handled
 * by [ValidationExceptionHandler].
 */
class PropertyValidationException : ValidationException {

    val bindingResult: BindingResult?

    constructor(bindingResult: BindingResult): super() {
        this.bindingResult = bindingResult
    }

    constructor(message: String): super(message) {
        this.bindingResult = null
    }
}

