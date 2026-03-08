package com.myheart.doctor.client;

import com.myheart.common.dto.LabResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "lab-service",
    path = "/api/lab",
    contextId = "doctorLabClient",
    fallback = com.myheart.doctor.client.fallback.LabServiceFallback.class
)
public interface LabServiceClient {
    
    @GetMapping("/results/patient/{patientId}")
    List<LabResultDTO> getPatientLabResults(@PathVariable("patientId") String patientId);
    

    @GetMapping("/results/doctor/{doctorId}/pending")
    List<LabResultDTO> getPendingLabResults(@PathVariable("doctorId") String doctorId);
}