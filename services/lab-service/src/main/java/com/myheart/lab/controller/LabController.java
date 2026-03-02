package com.myheart.lab.controller;

import com.myheart.common.constants.RabbitMQConstants;
import com.myheart.common.dto.NotificationDTO;
import com.myheart.lab.dto.LabResultRequestDTO;
import com.myheart.lab.dto.LabResultResponseDTO;
import com.myheart.lab.entity.LabResult;
import com.myheart.lab.service.LabService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
public class LabController {
    
    private final LabService labService;
    private final RabbitTemplate rabbitTemplate;
    
    @PostMapping("/results")
    public ResponseEntity<LabResultResponseDTO> createLabResult(@RequestBody LabResultRequestDTO request) {
        LabResultResponseDTO response = labService.createLabResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/results")
    public ResponseEntity<List<LabResultResponseDTO>> getAllLabResults() {
        return ResponseEntity.ok(labService.getAllLabResults());
    }
    
    @GetMapping("/results/{id}")
    public ResponseEntity<LabResultResponseDTO> getLabResultById(@PathVariable String id) {
        return ResponseEntity.ok(labService.getLabResultById(id));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabResultResponseDTO>> getLabResultsByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(labService.getLabResultsByPatient(patientId));
    }
    
    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<LabResultResponseDTO>> getPendingForDoctor(@PathVariable String doctorId) {
        List<LabResultResponseDTO> results = labService.getPendingByDoctor(doctorId)
                .stream()
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
    
    @PostMapping("/results/{id}/complete")
    public ResponseEntity<LabResultResponseDTO> completeLabResult(
            @PathVariable String id,
            @RequestBody Map<String, Object> results) {
        
        LabResult completed = labService.completeLabResult(id, results);
        
        // Envoyer une notification au médecin via RabbitMQ
        NotificationDTO notification = NotificationDTO.builder()
                .userId(completed.getDoctorId())
                .type("LAB_RESULT_READY")
                .channel("EMAIL")
                .subject("Résultat de laboratoire disponible")
                .content("Un résultat de laboratoire est disponible pour révision")
                .data(Map.of(
                    "labResultId", completed.getId(),
                    "patientId", completed.getPatientId(),
                    "patientName", completed.getPatientName(),
                    "testType", completed.getTestType()
                ))
                .build();
        
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.NOTIFICATION_EMAIL_ROUTING_KEY,
                notification
        );
        
        return ResponseEntity.ok(LabResultResponseDTO.fromEntity(completed));
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Lab service is running");
    }
}