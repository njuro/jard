package com.github.njuro.jard.user;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when looked up user is not found.
 *
 * <p>Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
public class UserNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 717247300800921506L;
}
