package com.github.njuro.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Thread not found")
public class ThreadNotFoundException extends RuntimeException {
}
