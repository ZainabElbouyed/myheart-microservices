// services/prescription-service/src/main/java/com/myheart/prescription/PrescriptionApplication.java
package com.myheart.prescription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;  
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
@EnableFeignClients
@EnableScheduling
@EnableDiscoveryClient  
public class PrescriptionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrescriptionApplication.class, args);
    }
}