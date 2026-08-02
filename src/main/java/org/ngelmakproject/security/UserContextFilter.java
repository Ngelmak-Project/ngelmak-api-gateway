package org.ngelmakproject.security;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class UserContextFilter implements GatewayFilter {

	private static final Logger log = LoggerFactory.getLogger(UserContextFilter.class);

	private final JwtUtil jwtUtil;

	public UserContextFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
			return chain.filter(exchange); // skip authentication
		}

		String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");

		Optional<Claims> optional = jwtUtil.tryParseClaims(token);

		if (optional.isPresent()) {
			String userId = optional.get().getSubject();
			String login = optional.get().get("login", String.class);
			String authorities = optional.get().get("authorities", String.class);

			log.debug("\n" +
					"=========< User Context Filter >=========\n" +
					"X-User-Id          : {}\n" +
					"X-User-Login       : {}\n" +
					"X-User-Authorities : {}\n" +
					"=========================================", userId, login, authorities);

			// Forward user info to downstream services
			ServerHttpRequest mutatedRequest = request.mutate()
					.header("X-User-Id", userId)
					.header("X-User-Login", login)
					.header("X-User-Authorities", authorities)
					.build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());
		}

		return chain.filter(exchange);
	}
}
