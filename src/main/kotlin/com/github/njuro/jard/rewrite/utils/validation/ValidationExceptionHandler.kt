package com.github.njuro.jard.rewrite.utils.validation

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Handler for validation exceptions.
 *
 * @see PropertyValidationException
 */
@RestControllerAdvice
class ValidationExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = ResponseEntity(ValidationErrors(ex.bindingResult), headers, status)


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PropertyValidationException::class)
    private fun handleValidationException(ex: PropertyValidationException): ValidationErrors =
        if (ex.bindingResult != null) ValidationErrors(ex.bindingResult) else ValidationErrors(ex.message!!)

}
