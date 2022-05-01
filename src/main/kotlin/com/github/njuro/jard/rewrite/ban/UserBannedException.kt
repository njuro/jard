package com.github.njuro.jard.rewrite.ban

import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ResponseStatus

/** Exception to be thrown when banned user attempts to post.  */
@ResponseStatus(code = FORBIDDEN)
class UserBannedException(message: String) : RuntimeException(message)
