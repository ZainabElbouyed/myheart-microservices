package com.myheart.appointment;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;  // ← AJOUTER CET IMPORT
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
@EnableScheduling
@EnableRabbit
@EnableDiscoveryClient  // ← AJOUTER CETTE ANNOTATION
public class AppointmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppointmentApplication.class, args);
    }
}