package org.ngelmakproject.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

/**
 * Utility class for only validating JWT tokens using JJWT.
 */
@Component
public class JwtUtil {

  private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

  private final SecretKey secretKey;

  /**
   * Initializes the secret key from a base64-encoded string in application
   * properties.
   * Example: jwt.secret=base64-encoded-256-bit-key
   */
  public JwtUtil(@Value("${jwt.secret}") String secret) {
    // Decode base64 secret key
    byte[] keyBytes = Base64.getDecoder()
        .decode(secret.getBytes(StandardCharsets.UTF_8));
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Validates the JWT token and returns the claims.
   * 
   * @param token JWT token to validate
   * @throws JwtException if the token is invalid or expired
   */
  public Claims validateToken(String token) {
    log.debug("Validate the JWT token {}", token);
    try {
      return Jwts.parser().verifyWith(secretKey)
          .build()
          .parseSignedClaims(token).getPayload();
    } catch (SignatureException e) {
      throw new JwtException("Invalid JWT signature");
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT");
    }
  }
}