package com.github.njuro.jard.rewrite.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.njuro.jard.rewrite.common.JWT_REMEMBER_ME_ATTRIBUTE
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** Custom filter which enables to receive authentication request in JSON form.  */
@Component
class JsonUsernamePasswordAuthenticationFilter(private val objectMapper: ObjectMapper) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication =
        try {
            val login = objectMapper.readValue(request.reader, LoginRequest::class.java)
            val authRequest = UsernamePasswordAuthenticationToken(login.username, login.password)
            setDetails(request, authRequest)
            request.setAttribute(JWT_REMEMBER_ME_ATTRIBUTE, login.rememberMe)
            authenticationManager.authenticate(authRequest)
        } catch (ex: IOException) {
            throw InternalAuthenticationServiceException("Parsing login request failed: ${ex.message}")
        }

    data class LoginRequest(val username: String, val password: String, val rememberMe: Boolean = false)
}
