package com.myheart.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter implements GlobalFilter, Ordered {
    
    // Structure pour stocker les informations de rate limiting
    private static class RateLimitInfo {
        private int count;
        private long windowStart;
        
        public RateLimitInfo() {
            this.count = 1;
            this.windowStart = Instant.now().getEpochSecond();
        }
        
        public int getCount() { return count; }
        public long getWindowStart() { return windowStart; }
        public void incrementCount() { this.count++; }
    }
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        String key = "rate-limit:" + clientIp;
        long currentSecond = Instant.now().getEpochSecond();
        
        // Récupérer ou créer les informations de rate limiting
        RateLimitInfo info = rateLimitMap.compute(key, (k, v) -> {
            if (v == null || currentSecond - v.getWindowStart() >= 60) {
                // Nouvelle fenêtre de 60 secondes
                return new RateLimitInfo();
            } else {
                v.incrementCount();
                return v;
            }
        });
        
        // Limite de 100 requêtes par minute
        if (info.getCount() <= 100) {
            return chain.filter(exchange);
        } else {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            return onRateLimitExceeded(exchange);
        }
    }
    
    private Mono<Void> onRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("Retry-After", "60");
        
        String errorBody = "{\"error\": \"Rate limit exceeded. Please try again later.\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }
    
    @Override
    public int getOrder() {
        return -3; // Exécuter avant les autres filtres
    }
}