package com.myheart.appointment.controller;

import com.myheart.appointment.dto.*;
import com.myheart.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable String id) {
        AppointmentResponseDTO response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPatient(
            @PathVariable String patientId) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointmentsForPatient(
            @PathVariable String patientId) {
        List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingAppointmentsForPatient(patientId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDoctor(
            @PathVariable String doctorId) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointmentsForDoctor(
            @PathVariable String doctorId) {
        List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingAppointmentsForDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<AppointmentResponseDTO>> getDoctorAppointmentsByDate(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentResponseDTO> appointments = appointmentService.getDoctorAppointmentsByDate(doctorId, date);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<AppointmentResponseDTO>> getPatientAppointmentsByDate(
            @PathVariable String patientId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentResponseDTO> appointments = appointmentService.getPatientAppointmentsByDate(patientId, date);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<AppointmentResponseDTO>> getTodayAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getTodayAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable String id,
            @Valid @RequestBody AppointmentUpdateDTO request) {
        AppointmentResponseDTO response = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody AppointmentStatusUpdateDTO request) {
        AppointmentResponseDTO response = appointmentService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirmAppointment(@PathVariable String id) {
        AppointmentResponseDTO response = appointmentService.confirmAppointment(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        AppointmentResponseDTO response = appointmentService.cancelAppointment(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable String id) {
        AppointmentResponseDTO response = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/checkin")
    public ResponseEntity<AppointmentResponseDTO> checkIn(@PathVariable String id) {
        AppointmentResponseDTO response = appointmentService.checkIn(id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        boolean available = appointmentService.isDoctorAvailable(doctorId, start, end);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getStatusStatistics() {
        return ResponseEntity.ok(appointmentService.getStatusStatistics());
    }
    
    @GetMapping("/stats/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(appointmentService.getMonthlyStats(year, month));
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Appointment service is running");
    }
}