package com.github.njuro.jard.rewrite.config.security.sba

import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.rewrite.common.SBA_SECRET_HEADER
import com.github.njuro.jard.user.UserAuthority
import com.github.njuro.jard.user.UserAuthority.ACTUATOR_ACCESS
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter for authentication between Spring Boot Admin server and client instances. Secret key is
 * stored in HTTP header with name defined in [Constants.SBA_SECRET_HEADER]. On successful
 * extraction and validation of secret key, fictional "user" with name is authenticated with single
 * authority - [UserAuthority.ACTUATOR_ACCESS] enabling him to access Spring Actuator
 * endpoints.
 */
@Component
@ConditionalOnProperty(name = ["spring.boot.admin.client.enabled"], havingValue = "true", matchIfMissing = true)
class SpringBootAdminAuthenticationFilter(
    @Value("\${spring.boot.admin.context-path}") private val sbaContextPath: String,
    @Value("\${management.endpoints.web.base-path}") private val actuatorBasePath: String,
    @Value("\${app.sba.secret}") private val sbaSecret: String
) : OncePerRequestFilter() {


    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        (!request.requestURI.startsWith(actuatorBasePath) && !request.requestURI.startsWith(sbaContextPath))

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val secret = request.getHeader(SBA_SECRET_HEADER) ?: return
            if (secret == sbaSecret) {
                val authentication =
                    UsernamePasswordAuthenticationToken("SBA_USER", null, listOf(ACTUATOR_ACCESS))
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            Companion.logger.error { "Error getting SBA secret code: ${ex.message}" }
        }

        filterChain.doFilter(request, response)
    }

    companion object: KLogging()
}
