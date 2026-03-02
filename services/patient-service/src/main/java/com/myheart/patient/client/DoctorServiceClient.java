// services/patient-service/src/main/java/com/myheart/patient/client/DoctorServiceClient.java
package com.myheart.patient.client;

import com.myheart.common.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "doctor-service", url = "${doctor.service.url:http://localhost:8083}")
public interface DoctorServiceClient {
    
    @GetMapping("/api/doctors")
    List<DoctorDTO> getAllDoctors();
    
    @GetMapping("/api/doctors/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") String id);
    
    @GetMapping("/api/doctors/specialty/{specialty}")
    List<DoctorDTO> getDoctorsBySpecialty(@PathVariable("specialty") String specialty);
}