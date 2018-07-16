package com.github.njuro.jboard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when looked up post is not found.
 * <p>
 * Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Post not found")
public class PostNotFoundException extends RuntimeException {
}
