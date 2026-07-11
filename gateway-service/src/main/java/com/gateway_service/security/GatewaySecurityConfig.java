package com.gateway_service.security;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec.anyExchange().permitAll() // Allow the Global Filter to handle the authentication
                ).build();
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-account-service", r -> r.path("/api/auth/**", "/api/users/**", "/api/accounts/**")
                        .filters(f->f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://user-account-service"))

                .route("transaction-service", r -> r.path("/api/transactions/**")
                        .filters(f->f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://transaction-service"))
                .build();

    }
}
