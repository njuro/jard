package com.github.njuro.jard.rewrite.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.njuro.jard.config.security.NoRedirectStrategy
import com.github.njuro.jard.config.security.jwt.JwtTokenProvider
import com.github.njuro.jard.rewrite.common.JWT_REMEMBER_ME_ATTRIBUTE
import com.github.njuro.jard.rewrite.utils.writeJson
import com.github.njuro.jard.user.User
import com.github.njuro.jard.user.UserMapper
import com.github.njuro.jard.user.UserService
import com.github.njuro.jard.user.dto.UserDto.PublicView
import mu.KLogging
import org.apache.tomcat.util.http.CookieProcessor
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.lang.Boolean.parseBoolean
import java.time.OffsetDateTime
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoginSuccessHandler(
    private val userService: UserService,
    private val userMapper: UserMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val cookieProcessor: CookieProcessor,
    private val objectMapper: ObjectMapper
) : SimpleUrlAuthenticationSuccessHandler() {
    init {
        redirectStrategy = NoRedirectStrategy()
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        val user = (authentication.principal as User).apply {
            lastLoginIp = request.remoteAddr
            lastLogin = OffsetDateTime.now()
        }
        userService.saveUser(user)
        Companion.logger.debug { "User ${user.username} logged from IP ${user.lastLoginIp}" }

        super.onAuthenticationSuccess(request, response, authentication)

        val rememberMe = parseBoolean(request.getAttribute(JWT_REMEMBER_ME_ATTRIBUTE).toString())
        val cookie: Cookie =
            if (rememberMe)
                jwtTokenProvider.generateRememberMeCookie(authentication)
            else
                jwtTokenProvider.generateSessionCookie(authentication)
        response.addHeader(SET_COOKIE, cookieProcessor.generateHeader(cookie, request))
        response.writeJson(objectMapper, userMapper.toDto(userService.currentUser), PublicView::class.java)
    }

    companion object: KLogging()
}
