package com.github.njuro.jard.rewrite.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.njuro.jard.rewrite.utils.writeJson
import com.github.njuro.jard.utils.HttpUtils
import com.github.njuro.jard.utils.validation.ValidationErrors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoginFailureHandler(private val objectMapper: ObjectMapper) : AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException
    ) {
        response.status = UNAUTHORIZED.value()
        response.writeJson(objectMapper, ValidationErrors("Authentication failed: ${exception.message}"))
    }
}
