package com.github.njuro.jard.rewrite.config.security.jwt

import com.github.njuro.jard.rewrite.common.JWT_COOKIE_NAME
import com.github.njuro.jard.user.UserFacade
import mu.KLogging
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** Filter which handles authentication via JSON Web Token (JWT) cookies.  */
@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider,
    @Lazy private val userFacade: UserFacade
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = request.getJwtCookie() ?: return
            if (tokenProvider.validateToken(jwt)) {
                val username = tokenProvider.getUsernameFromJWT(jwt)
                val userDetails = userFacade.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            Companion.logger.error(ex) { "Could not set user authentication in security context" }
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Finds JWT cookie and extracts token from it.
     *
     * @param request incoming HTTP request
     * @return JWT, or `null` if no token was found
     */
    private fun HttpServletRequest.getJwtCookie(): String? = cookies?.find { it.name == JWT_COOKIE_NAME }?.value



    companion object: KLogging()
}
