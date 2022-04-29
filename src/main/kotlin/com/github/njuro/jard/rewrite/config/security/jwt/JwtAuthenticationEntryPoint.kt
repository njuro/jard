package com.github.njuro.jard.rewrite.config.security.jwt

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

/** Handles AuthenticationException on failed JWT authentication.  */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        ex: AuthenticationException
    ) {
        httpServletResponse.sendError(SC_UNAUTHORIZED, ex.message)
    }
}
