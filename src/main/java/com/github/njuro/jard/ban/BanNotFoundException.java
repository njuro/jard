package com.github.njuro.jard.ban;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when looked up ban is not found.
 *
 * <p>Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Ban not found")
public class BanNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 717247300800921506L;
}
