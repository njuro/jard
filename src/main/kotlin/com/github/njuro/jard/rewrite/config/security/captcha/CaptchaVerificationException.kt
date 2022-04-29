package com.github.njuro.jard.rewrite.config.security.captcha

import com.github.njuro.jard.utils.validation.PropertyValidationException

/**
 * Exception to be thrown when captcha verification fails. Handled by [ ].
 *
 *
 * Note: This exception isn't thrown by [CaptchaProvider] itself, but on appropriate places
 * in business logic, where verification failure needs to be propagated to client.
 */
object CaptchaVerificationException : PropertyValidationException("Captcha verification failed")
