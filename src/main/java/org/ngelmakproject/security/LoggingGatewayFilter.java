package org.ngelmakproject.security;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class LoggingGatewayFilter implements GatewayFilter {
  
  private static final Logger log = LoggerFactory.getLogger(LoggingGatewayFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String originalPath = exchange.getRequest().getPath().toString();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            String routeId = Optional.ofNullable(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR))
                .map(Object::toString).orElse("unknown");

            URI targetUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);

            log.info("Incoming request: {}", originalPath);
            log.info("Matched route: {}", routeId);
            log.info("➡️ Forwarded to: {}", targetUri);
        }));
  }
}
