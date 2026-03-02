package com.myheart.patient.controller;

import com.myheart.patient.dto.PatientRequestDTO;
import com.myheart.patient.dto.PatientResponseDTO;
import com.myheart.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    
    private final PatientService patientService;
    
    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO request) {
        PatientResponseDTO response = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable String id) {
        PatientResponseDTO response = patientService.getPatientById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<PatientResponseDTO> getPatientByEmail(@PathVariable String email) {
        PatientResponseDTO response = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(@RequestParam String q) {
        List<PatientResponseDTO> patients = patientService.searchPatients(q);
        return ResponseEntity.ok(patients);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientRequestDTO request) {
        PatientResponseDTO response = patientService.updatePatient(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PatientResponseDTO> deactivatePatient(@PathVariable String id) {
        PatientResponseDTO response = patientService.deactivatePatient(id);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<PatientResponseDTO> activatePatient(@PathVariable String id) {
        PatientResponseDTO response = patientService.activatePatient(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", patientService.getAllPatients().size());
        stats.put("active", patientService.getActivePatientCount());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Patient service is running");
    }
}