package com.myheart.lab.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.myheart.lab.client")
public class FeignConfig {
    
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // BASIC, HEADERS, ou FULL
    }
    
    @Bean
    ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }
}