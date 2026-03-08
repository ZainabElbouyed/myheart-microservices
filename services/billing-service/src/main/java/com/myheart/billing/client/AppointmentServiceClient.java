package com.myheart.billing.client;

import com.myheart.common.dto.AppointmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "appointment-service",
    path = "/api/appointments",
    contextId = "billingAppointmentClient",
    fallback = com.myheart.billing.client.fallback.AppointmentServiceFallback.class
)
public interface AppointmentServiceClient {
    
    @GetMapping("/{id}")
    AppointmentDTO getAppointmentById(@PathVariable("id") String id);
}