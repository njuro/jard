package com.github.njuro.jard.rewrite.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exception to be thrown when looked up user is not found.
 *
 *
 * Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
class UserNotFoundException : RuntimeException()
