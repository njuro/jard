package com.github.njuro.jboard.config.security.jwt;

import com.github.njuro.jboard.common.Constants;
import com.github.njuro.jboard.user.User;
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

  public String generateToken(Authentication authentication) {

    User user = (User) authentication.getPrincipal();

    var now = new Date();
    var expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public Cookie generateSessionCookie(Authentication authentication) {
    return generateCookie(authentication, -1);
  }

  public Cookie generateRememberMeCookie(Authentication authentication) {
    return generateCookie(authentication, jwtExpiration);
  }

  private Cookie generateCookie(Authentication authentication, int maxAge) {
    var cookie = new Cookie(Constants.JWT_COOKIE_NAME, generateToken(authentication));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);

    return cookie;
  }

  public String getUsernameFromJWT(String token) {
    Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

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
