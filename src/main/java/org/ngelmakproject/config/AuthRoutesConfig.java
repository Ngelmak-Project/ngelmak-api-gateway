package org.ngelmakproject.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthRoutesConfig {

  @Bean
  public RouteLocator authRoutes(RouteLocatorBuilder builder) {
    // Map the /auth/** path to the auth service, rewriting the path to /api/v1/** for internal routing.
    return builder.routes()
        .route("auth", r -> r
            .path("/auth/**")
            .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/api/v1/${segment}"))
            .uri("http://auth:37273"))
        .build();
  }
}
