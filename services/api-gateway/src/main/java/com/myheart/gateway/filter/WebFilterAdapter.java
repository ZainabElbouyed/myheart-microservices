package com.myheart.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Adaptateur pour convertir un GlobalFilter (Spring Cloud Gateway) en WebFilter (Spring WebFlux)
 */
public class WebFilterAdapter implements WebFilter {
    
    private final AuthenticationFilter authenticationFilter;
    
    public WebFilterAdapter(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Convertir WebFilterChain en GatewayFilterChain
        GatewayFilterChain gatewayChain = new GatewayFilterChain() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange) {
                return chain.filter(exchange);
            }
        };
        
        // Appeler le filtre d'authentification
        return authenticationFilter.filter(exchange, gatewayChain);
    }
}