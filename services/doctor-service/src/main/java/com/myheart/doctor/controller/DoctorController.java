package com.myheart.doctor.controller;


import com.myheart.doctor.dto.DoctorRequestDTO;
import com.myheart.doctor.dto.DoctorResponseDTO;
import com.myheart.doctor.entity.Doctor;
import com.myheart.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(@Valid @RequestBody DoctorRequestDTO request) {
        DoctorResponseDTO response = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable String id) {
        DoctorResponseDTO response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorResponseDTO> getDoctorByEmail(@PathVariable String email) {
        DoctorResponseDTO response = doctorService.getDoctorByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/license/{licenseNumber}")
    public ResponseEntity<DoctorResponseDTO> getDoctorByLicenseNumber(@PathVariable String licenseNumber) {
        DoctorResponseDTO response = doctorService.getDoctorByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        List<DoctorResponseDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(@RequestParam String q) {
        List<DoctorResponseDTO> doctors = doctorService.searchDoctors(q);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsBySpecialty(@PathVariable String specialty) {
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsByDepartment(@PathVariable String department) {
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsByCity(@PathVariable String city) {
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsByCity(city);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<DoctorResponseDTO>> getAvailableDoctors() {
        List<DoctorResponseDTO> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<DoctorResponseDTO>> getTopRatedDoctors() {
        List<DoctorResponseDTO> doctors = doctorService.getTopRatedDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/max-fee")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsByMaxFee(@RequestParam BigDecimal maxFee) {
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsByMaxFee(maxFee);
        return ResponseEntity.ok(doctors);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable String id,
            @Valid @RequestBody DoctorRequestDTO request) {
        DoctorResponseDTO response = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<DoctorResponseDTO> updateDoctorStatus(
            @PathVariable String id,
            @RequestParam Doctor.DoctorStatus status) {
        DoctorResponseDTO response = doctorService.updateDoctorStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/accepting-patients")
    public ResponseEntity<DoctorResponseDTO> updateAcceptingPatients(
            @PathVariable String id,
            @RequestParam boolean accepting) {
        DoctorResponseDTO response = doctorService.updateAcceptingPatients(id, accepting);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/rating")
    public ResponseEntity<DoctorResponseDTO> addRating(
            @PathVariable String id,
            @RequestParam Double rating) {
        DoctorResponseDTO response = doctorService.updateRating(id, rating);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long activeCount = doctorService.getActiveDoctorCount();
        long totalCount = doctorService.getAllDoctors().size();
        
        return ResponseEntity.ok(Map.of(
            "total", totalCount,
            "active", activeCount,
            "inactive", totalCount - activeCount
        ));
    }
    
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String id,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        
        log.info("Checking availability for doctor {} from {} to {}", id, start, end);
        boolean available = doctorService.isDoctorAvailable(id, start, end);
        return ResponseEntity.ok(available);
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Doctor service is running");
    }
}