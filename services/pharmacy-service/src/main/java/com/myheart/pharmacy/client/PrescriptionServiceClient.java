// services/pharmacy-service/src/main/java/com/myheart/pharmacy/client/PrescriptionServiceClient.java
package com.myheart.pharmacy.client;

import com.myheart.common.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "prescription-service",
    path = "/api/prescriptions",
    contextId = "pharmacyPrescriptionClient",
    fallback = com.myheart.pharmacy.client.fallback.PrescriptionServiceFallback.class
)
public interface PrescriptionServiceClient {
    
    @GetMapping("/status/{status}")
    List<PrescriptionDTO> getPrescriptionsByStatus(@PathVariable("status") String status);
    
    @GetMapping("/patient/{patientId}")
    List<PrescriptionDTO> getPatientPrescriptions(@PathVariable("patientId") String patientId);
    
    @GetMapping("/{id}")
    PrescriptionDTO getPrescriptionById(@PathVariable("id") String id);
    
    @PostMapping("/{id}/fill")
    PrescriptionDTO fillPrescription(
            @PathVariable("id") String id,
            @RequestParam("pharmacyId") String pharmacyId,      
            @RequestParam("pharmacist") String pharmacist);
}