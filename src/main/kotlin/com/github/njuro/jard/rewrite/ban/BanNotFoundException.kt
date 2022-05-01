package com.github.njuro.jard.rewrite.ban

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exception to be thrown when looked up ban is not found.
 *
 *
 * Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Ban not found")
class BanNotFoundException : RuntimeException()
