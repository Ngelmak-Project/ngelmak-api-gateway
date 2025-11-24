package org.ngelmakproject.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
      return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
    }

    String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
    
    try {
      Claims claims = jwtUtil.validateToken(token);

      // Forward user info to downstream services
      ServerHttpRequest mutatedRequest = request.mutate()
          .header("X-User-Username", claims.getSubject())
          .header("X-User-Roles", claims.get("authorities", String.class))
          .build();

      return chain.filter(exchange.mutate().request(mutatedRequest).build());

    } catch (Exception e) {
      return onError(exchange, "Invalid JWT", HttpStatus.UNAUTHORIZED);
    }
  }

  private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
    exchange.getResponse().setStatusCode(status);
    return exchange.getResponse().setComplete();
  }
}
