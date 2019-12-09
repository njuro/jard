package com.github.njuro.jboard.config.security.jwt;

import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import javax.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${app.jwt.secret:secretkey}")
  private String jwtSecret;

  @Value("${app.jwt.expiration:604800}")
  private int jwtExpiration;

  public String generateToken(final Authentication authentication) {

    final User user = (User) authentication.getPrincipal();

    final Date now = new Date();
    final Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public Cookie generateSessionCookie(final Authentication authentication) {
    return generateCookie(authentication, -1);
  }

  public Cookie generateRememberMeCookie(final Authentication authentication) {
    return generateCookie(authentication, jwtExpiration);
  }

  private Cookie generateCookie(final Authentication authentication, final int maxAge) {
    final Cookie cookie = new Cookie(Constants.JWT_COOKIE_NAME, generateToken(authentication));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);

    return cookie;
  }

  public String getUsernameFromJWT(final String token) {
    final Claims claims =
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

  public boolean validateToken(final String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (final SignatureException ex) {
      log.error("Invalid JWT signature");
    } catch (final MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (final ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (final UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (final IllegalArgumentException ex) {
      log.error("JWT claims string is empty.");
    }
    return false;
  }
}
