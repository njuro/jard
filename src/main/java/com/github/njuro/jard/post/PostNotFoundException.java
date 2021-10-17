package com.github.njuro.jard.post;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when looked up post is not found.
 *
 * <p>Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Post not found")
public class PostNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = -8401273053639758133L;
}
