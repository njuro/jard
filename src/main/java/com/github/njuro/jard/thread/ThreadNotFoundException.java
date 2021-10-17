package com.github.njuro.jard.thread;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when looked up thread is not found.
 *
 * <p>Set 404 HTTP status.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Thread not found")
public class ThreadNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 4281510962414008488L;
}
