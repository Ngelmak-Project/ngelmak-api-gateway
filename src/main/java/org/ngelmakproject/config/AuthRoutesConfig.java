package org.ngelmakproject.config;

import org.ngelmakproject.security.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthRoutesConfig {

  private final JwtAuthenticationFilter jwtFilter;

  public AuthRoutesConfig(JwtAuthenticationFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public RouteLocator authRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        // 🔓 Public Auth Routes
        .route("auth-public", r -> r
            .path("/api/auth/authenticate", "/api/auth/register")
            .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/api/${segment}"))
            .uri("http://ngelmak-auth:4042"))

        // 🔐 Protected Auth Routes
        .route("auth-protected", r -> r
            .path("/api/auth/**")
            .filters(f -> f
                .filter(jwtFilter)
                .rewritePath("/api/auth/(?<segment>.*)", "/api/${segment}"))
            .uri("http://ngelmak-auth:4042"))

        .build();
  }
}
