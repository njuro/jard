package com.github.njuro.jard.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when looked up board is not found.
 *
 * <p>Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Board not found")
public class BoardNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 2693252281905504956L;
}
