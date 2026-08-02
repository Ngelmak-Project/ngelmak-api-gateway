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
				.route("core", r -> r
						.path("/core/**")
						.filters(f -> f
								.filter(userContextFilter)
								.rewritePath("/core/(?<segment>.*)", "/api/v1/${segment}"))
						.uri("http://core:37373"))
				.build();
	}
}
