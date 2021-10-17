package com.github.njuro.jard.config.security.jwt;

import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.user.User;
import io.jsonwebtoken.*;
import java.util.Date;
import javax.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/** Class for generating and validating JSON Web Tokens (JWT). */
@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${app.jwt.secret:secretkey}")
  private String jwtSecret;

  @Value("${app.jwt.expiration:604800}")
  private int jwtExpiration;

  /**
   * Generates JWT for current user.
   *
   * @param authentication current user
   * @return generated token
   */
  public String generateToken(Authentication authentication) {

    var user = (User) authentication.getPrincipal();

    var now = new Date();
    var expiryDate = new Date(now.getTime() + jwtExpiration * 1000L);

    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  /**
   * Generates JWT cookie for current user which will be valid only for the current session.
   *
   * @param authentication current user
   * @return generated HTTP cookie with token
   */
  public Cookie generateSessionCookie(Authentication authentication) {
    return generateCookie(authentication, -1);
  }

  /**
   * Generates JWT for current user which will be valid for set period (akka "Remember Me").
   *
   * @param authentication current user
   * @return generated HTTP cookie with token
   */
  public Cookie generateRememberMeCookie(Authentication authentication) {
    return generateCookie(authentication, jwtExpiration);
  }

  /**
   * Generates JWT http only cookie for current user with given expiration time.
   *
   * @param authentication current user
   * @param maxAge {@link Cookie#setMaxAge(int)}
   * @return generated HTTP cookie with token
   */
  private Cookie generateCookie(Authentication authentication, int maxAge) {
    var cookie = new Cookie(Constants.JWT_COOKIE_NAME, generateToken(authentication));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);

    return cookie;
  }

  /**
   * Retrieves name of ther user the given JWT was issued to.
   *
   * @param token JWT token
   * @return username from the token
   */
  public String getUsernameFromJWT(String token) {
    Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

  /**
   * Validates JWT.
   *
   * @param authToken token to validate
   * @return true if token is valid and not expired, false otherwise
   */
  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException ex) {
      log.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty.");
    }
    return false;
  }
}
