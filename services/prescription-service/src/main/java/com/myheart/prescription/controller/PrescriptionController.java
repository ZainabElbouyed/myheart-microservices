package com.myheart.prescription.controller;

import com.myheart.prescription.dto.PrescriptionRequestDTO;
import com.myheart.prescription.dto.PrescriptionResponseDTO;
import com.myheart.prescription.entity.Prescription;
import com.myheart.prescription.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    
    private final PrescriptionService prescriptionService;
    
    @PostMapping
    public ResponseEntity<PrescriptionResponseDTO> createPrescription(@Valid @RequestBody PrescriptionRequestDTO request) {
        PrescriptionResponseDTO response = prescriptionService.createPrescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PrescriptionResponseDTO>> getAllPrescriptions() {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDTO> getPrescriptionById(@PathVariable String id) {
        PrescriptionResponseDTO response = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/number/{prescriptionNumber}")
    public ResponseEntity<PrescriptionResponseDTO> getPrescriptionByNumber(@PathVariable String prescriptionNumber) {
        PrescriptionResponseDTO response = prescriptionService.getPrescriptionByNumber(prescriptionNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getPrescriptionsByPatient(@PathVariable String patientId) {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<List<PrescriptionResponseDTO>> getActivePrescriptionsByPatient(@PathVariable String patientId) {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getActivePrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/patient/{patientId}/valid")
    public ResponseEntity<List<PrescriptionResponseDTO>> getValidPrescriptionsByPatient(@PathVariable String patientId) {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getValidPrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getPrescriptionsByDoctor(@PathVariable String doctorId) {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getPrescriptionsByStatus(@PathVariable Prescription.PrescriptionStatus status) {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getPrescriptionsByStatus(status);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/expired")
    public ResponseEntity<List<PrescriptionResponseDTO>> getExpiredPrescriptions() {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getExpiredPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PrescriptionResponseDTO>> searchByPatientAndMedication(
            @RequestParam String patientId,
            @RequestParam String medication) {
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.searchByPatientAndMedication(patientId, medication);
        return ResponseEntity.ok(prescriptions);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDTO> updatePrescription(
            @PathVariable String id,
            @Valid @RequestBody PrescriptionRequestDTO request) {
        PrescriptionResponseDTO response = prescriptionService.updatePrescription(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<PrescriptionResponseDTO> updatePrescriptionStatus(
            @PathVariable String id,
            @RequestParam Prescription.PrescriptionStatus status) {
        PrescriptionResponseDTO response = prescriptionService.updatePrescriptionStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/fill")
    public ResponseEntity<PrescriptionResponseDTO> fillPrescription(
            @PathVariable String id,
            @RequestParam String pharmacyId,
            @RequestParam String pharmacyName) {
        PrescriptionResponseDTO response = prescriptionService.fillPrescription(id, pharmacyId, pharmacyName);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/refill")
    public ResponseEntity<PrescriptionResponseDTO> refillPrescription(@PathVariable String id) {
        PrescriptionResponseDTO response = prescriptionService.refillPrescription(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PrescriptionResponseDTO> cancelPrescription(
            @PathVariable String id,
            @RequestParam String reason) {
        PrescriptionResponseDTO response = prescriptionService.cancelPrescription(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable String id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = prescriptionService.getPrescriptionStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Prescription service is running");
    }
}