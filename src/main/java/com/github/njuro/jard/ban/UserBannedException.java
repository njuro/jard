package com.github.njuro.jard.ban;

import java.io.Serial;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception to be thrown when banned user attempts to post. */
@ResponseStatus(code = HttpStatus.FORBIDDEN)
@NoArgsConstructor
public class UserBannedException extends RuntimeException {
  @Serial private static final long serialVersionUID = 8772236780722701190L;

  public UserBannedException(String message) {
    super(message);
  }
}
