package org.ngelmakproject.config;

import org.ngelmakproject.security.JwtAuthenticationFilter;
import org.ngelmakproject.security.LoggingGatewayFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

  private final JwtAuthenticationFilter jwtFilter;

  public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  /**
   * Defines custom routes for Spring Cloud Gateway.
   * Each route maps an external path (used by REST clients) to an internal
   * service URI.
   * The JWT filter is applied to secure the routes, but only validates the token
   * — no role checks.
   *
   * Example mappings:
   * - REST CLIENT → http://localhost:4000/api/auth/** → API GATEWAY →
   * http://localhost:4041/auth/**
   * - REST CLIENT → http://localhost:4000/api/truthline-core/** → API GATEWAY →
   * http://localhost:4042/truthline-core/**
   */
  @Bean
  public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        /* Auth Service Route (🆓 Public route — no JWT) */
        .route("auth-service-public", r -> r.path("/api/auth/authenticate", "/api/auth/register")
            .filters(f -> f
                .rewritePath("/api/auth/(?<segment>.*)", "/api/${segment}"))
            .uri("http://localhost:4041"))

        /* Auth Service Route (🔐 Protected route — requires JWT) */
        .route("auth-service", r -> r.path("/api/auth/**")
            .filters(f -> f
                .filter(jwtFilter) // Validate JWT
                // .filter(new LoggingGatewayFilter())
                .rewritePath("/api/auth/(?<segment>.*)", "/api/${segment}"))
            .uri("http://localhost:4041")) // Forward to auth service
        // Truthline Core Service Route (🔐 Protected route — requires JWT)
        .route("truthline-service-core", r -> r.path("/api/truthline-core/**")
            .filters(f -> f
                .filter(jwtFilter) // Validate JWT
                .stripPrefix(1)) // Remove "/api" before forwarding
            .uri("http://localhost:4042")) // Forward to core service

        .build();
  }
}
