package com.myheart.gateway.handler;

import com.myheart.gateway.dto.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Order(-1)
@Configuration
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        ErrorResponse errorResponse;
        HttpStatus status;
        
        if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error("Service Not Found")
                .message("The requested service is not available")
                .path(exchange.getRequest().getURI().getPath())
                .build();
        } else if (ex instanceof ResponseStatusException) {
            // 🔴 CORRECTION : Convertir HttpStatusCode en HttpStatus
            HttpStatusCode statusCode = ((ResponseStatusException) ex).getStatusCode();
            status = HttpStatus.valueOf(statusCode.value());
            errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getURI().getPath())
                .build();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(exchange.getRequest().getURI().getPath())
                .build();
        }
        
        response.setStatusCode(status);
        
        byte[] bytes = errorResponse.toString().getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}