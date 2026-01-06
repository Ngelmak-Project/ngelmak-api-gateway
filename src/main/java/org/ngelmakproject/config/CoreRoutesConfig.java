package org.ngelmakproject.config;

import org.ngelmakproject.security.JwtAuthenticationFilter;
import org.ngelmakproject.security.UserContextFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreRoutesConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final UserContextFilter userContextFilter;

  public CoreRoutesConfig(JwtAuthenticationFilter jwtFilter, UserContextFilter userContextFilter) {
    this.jwtFilter = jwtFilter;
    this.userContextFilter = userContextFilter;
  }

  @Bean
  public RouteLocator coreRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        // 🔓 Public Core Routes
        .route("core-public", r -> r
            .path("/api/core/r/**")
            .filters(f -> f
                .filter(userContextFilter)
                .rewritePath("/api/core/r/(?<segment>.*)", "/api/${segment}"))
            .uri("http://ngelmak-core:5742"))

        // 🔐 Protected Core Routes
        .route("core-protected", r -> r
            .path("/api/core/**")
            .filters(f -> f
                .filter(jwtFilter)
                .rewritePath("/api/core/(?<segment>.*)", "/api/${segment}"))
            .uri("http://ngelmak-core:5742"))

        .build();
  }
}
