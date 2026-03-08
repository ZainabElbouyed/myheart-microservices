package com.myheart.billing.client;

import com.myheart.common.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "patient-service",
    path = "/api/patients",
    contextId = "billingPatientClient",
    fallback = com.myheart.billing.client.fallback.PatientServiceFallback.class
)
public interface PatientServiceClient {
    
    @GetMapping("/{id}")
    PatientDTO getPatientById(@PathVariable("id") String id);
}