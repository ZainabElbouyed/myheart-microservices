package com.myheart.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {
    
    @RequestMapping("/fallback/patient")
    public Mono<ResponseEntity<Map<String, Object>>> patientFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service patient temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/doctor")
    public Mono<ResponseEntity<Map<String, Object>>> doctorFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service docteur temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/appointment")
    public Mono<ResponseEntity<Map<String, Object>>> appointmentFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service rendez-vous temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/billing")
    public Mono<ResponseEntity<Map<String, Object>>> billingFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service facturation temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/lab")
    public Mono<ResponseEntity<Map<String, Object>>> labFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service laboratoire temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/prescription")
    public Mono<ResponseEntity<Map<String, Object>>> prescriptionFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service prescription temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/pharmacy")
    public Mono<ResponseEntity<Map<String, Object>>> pharmacyFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service pharmacie temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/notification")
    public Mono<ResponseEntity<Map<String, Object>>> notificationFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service notification temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    @RequestMapping("/fallback/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Service authentification temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
}