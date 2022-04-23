package com.github.njuro.jard.rewrite.utils.validation

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.ConstraintViolation

@Component
class PropertyValidator(private val validator: LocalValidatorFactoryBean) {
    /**
     * Validates JSR 303 annotations on given object.
     *
     * @param target object to validate
     * @throws PropertyValidationException if validation fails
     */
    fun validateObject(target: Any) {
        val bindingResult: BindingResult = BeanPropertyBindingResult(target, "request")
        validator.validate(target, bindingResult)
        if (bindingResult.hasFieldErrors()) {
            throw PropertyValidationException(bindingResult)
        }
    }

    /**
     * Validates JSR 303 annotations on given property.
     *
     * @param target object which contains property to be validated
     * @param property name of the property to be validated
     * @throws PropertyValidationException if validation fails
     */
    fun <T> validateProperty(target: T, property: String) {
        val violations = validator.validateProperty(target, property)
        if (violations.isNotEmpty()) {
            val errors = violations.joinToString(", ", transform = ConstraintViolation<T>::getMessage)
            throw PropertyValidationException(
                message = "Validation of property $property failed: $errors"
            )
        }
    }
}
