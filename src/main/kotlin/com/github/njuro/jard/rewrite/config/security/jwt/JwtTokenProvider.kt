package com.github.njuro.jard.rewrite.config.security.jwt

import com.github.njuro.jard.rewrite.common.JWT_COOKIE_NAME
import com.github.njuro.jard.user.User
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm.HS512
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.Date
import javax.servlet.http.Cookie

/** Class for generating and validating JSON Web Tokens (JWT).  */
@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret:secretkey}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration:604800}") private val jwtExpiration: Int
) {
    /**
     * Generates JWT for current user.
     *
     * @param authentication current user
     * @return generated token
     */
    fun generateToken(authentication: Authentication): String {
        val user = authentication.principal as User
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration * 1000L)
        return Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(HS512, jwtSecret)
            .compact()
    }

    /**
     * Generates JWT cookie for current user which will be valid only for the current session.
     *
     * @param authentication current user
     * @return generated HTTP cookie with token
     */
    fun generateSessionCookie(authentication: Authentication): Cookie = generateCookie(authentication, -1)


    /**
     * Generates JWT for current user which will be valid for set period (akka "Remember Me").
     *
     * @param authentication current user
     * @return generated HTTP cookie with token
     */
    fun generateRememberMeCookie(authentication: Authentication): Cookie = generateCookie(authentication, jwtExpiration)


    /**
     * Generates JWT http only cookie for current user with given expiration time.
     *
     * @param authentication current user
     * @param maxAge [Cookie.setMaxAge]
     * @return generated HTTP cookie with token
     */
    private fun generateCookie(authentication: Authentication, maxAge: Int): Cookie =
        Cookie(JWT_COOKIE_NAME, generateToken(authentication)).apply {
            path = "/"
            isHttpOnly = true
            this.maxAge = maxAge
        }

    /**
     * Retrieves name of ther user the given JWT was issued to.
     *
     * @param token JWT token
     * @return username from the token
     */
    fun getUsernameFromJWT(token: String): String {
        val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body
        return claims.subject
    }

    /**
     * Validates JWT.
     *
     * @param authToken token to validate
     * @return true if token is valid and not expired, false otherwise
     */
    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error { "Invalid JWT signature" }
        } catch (ex: MalformedJwtException) {
            logger.error { "Invalid JWT token" }
        } catch (ex: ExpiredJwtException) {
            logger.error { "Expired JWT token" }
        } catch (ex: UnsupportedJwtException) {
            logger.error { "Unsupported JWT token" }
        } catch (ex: IllegalArgumentException) {
            logger.error {  "JWT claims string is empty." }
        }

        return false
    }

    companion object: KLogging()
}
