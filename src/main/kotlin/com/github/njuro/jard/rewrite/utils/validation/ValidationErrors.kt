package com.github.njuro.jard.rewrite.utils.validation

import org.springframework.validation.BindingResult
import java.time.OffsetDateTime

/** Class for encapsulating validation errors and sending them to client.  */
@Suppress("unused")
class ValidationErrors {
    /** When the error occurred. */
    private val timestamp = OffsetDateTime.now()

    /** Map of the errors - key is name of invalid field, value is the field value. */
    private val errors: Map<String, String?>

    constructor(validationResult: BindingResult) {
        errors = validationResult.fieldErrors.associate { it.field to it.defaultMessage }
    }

    constructor(objectError: String) {
        errors = mapOf(OBJECT_ERROR_KEY to objectError)
    }

    companion object {
        /**
         * Special "field name" to be used if validation fails on the object itself (i.e. poster's IP is
         * banned).
         */
        const val OBJECT_ERROR_KEY = "object"
    }
}
