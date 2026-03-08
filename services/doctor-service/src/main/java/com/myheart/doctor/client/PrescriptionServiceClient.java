package com.myheart.doctor.client;

import com.myheart.common.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "prescription-service",
    path = "/api/prescriptions",
    contextId = "doctorPrescriptionClient",
    fallback = com.myheart.doctor.client.fallback.PrescriptionServiceFallback.class
)
public interface PrescriptionServiceClient {
    
    @GetMapping("/patient/{patientId}")
    List<PrescriptionDTO> getPatientPrescriptions(@PathVariable("patientId") String patientId);
}