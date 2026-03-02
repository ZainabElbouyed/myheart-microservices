// services/pharmacy-service/src/main/java/com/myheart/pharmacy/client/PrescriptionServiceClient.java
package com.myheart.pharmacy.client;

import com.myheart.common.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "prescription-service", url = "${prescription.service.url:http://localhost:8087}")
public interface PrescriptionServiceClient {
    
    @GetMapping("/api/prescriptions/status/{status}")
    List<PrescriptionDTO> getPrescriptionsByStatus(@PathVariable("status") String status);
    
    @GetMapping("/api/prescriptions/patient/{patientId}")
    List<PrescriptionDTO> getPatientPrescriptions(@PathVariable("patientId") String patientId);
    
    @GetMapping("/api/prescriptions/{id}")
    PrescriptionDTO getPrescriptionById(@PathVariable("id") String id);
    
    @PostMapping("/api/prescriptions/{id}/fill")
    PrescriptionDTO fillPrescription(
            @PathVariable("id") String id,
            @RequestParam String pharmacyId,
            @RequestParam String pharmacist);
}