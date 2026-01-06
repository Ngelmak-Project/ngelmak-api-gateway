package org.ngelmakproject.security;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utility class for generating and validating JWT tokens using JJWT 0.9.1.
 */
@Component
public class JwtUtil {

  private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
  
  private final SecretKey secretKey;

  public JwtUtil(@Value("${jwt-secret-key}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
  }

  /**
   * Validates the JWT token and returns the claims.
   * 
   * @param token JWT token to validate
   */
  public Claims validateToken(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    } catch (JwtException e) {
      log.warn("JWT validation failed: {}", e.getMessage());
      throw e;
    }
  }

  public Optional<Claims> tryParseClaims(String token) {
    try {
      return Optional.of(validateToken(token));
    } catch (JwtException e) {
      return Optional.empty();
    }
  }

}