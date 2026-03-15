package com.myheart.appointment.service;

import com.myheart.appointment.dto.AppointmentResponseDTO;
import com.myheart.common.dto.AppointmentDTO;
import com.myheart.common.dto.PatientDTO;
import com.myheart.common.dto.DoctorDTO;
import com.myheart.common.events.AppointmentEvent;
import jakarta.annotation.PostConstruct;  
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventPublisher {
    
    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;
    
    @Value("${spring.kafka.topics.appointment-events}")
    private String appointmentEventsTopic;
    
    @PostConstruct  
    public void init() {
        log.info("🟢 AppointmentEventPublisher initialized with topic: {}", appointmentEventsTopic);
        log.info("🟢 KafkaTemplate is null? {}", kafkaTemplate == null);
    }
    
    public void publishAppointmentCreated(AppointmentResponseDTO savedAppointmentDTO, PatientDTO patient, DoctorDTO doctor) {
        log.info("🔵 KAFKA PUBLISHER CALLED for appointment: {}", savedAppointmentDTO.getId());
        
        try {
            AppointmentEvent event = AppointmentEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("CREATED")
                    .appointmentId(savedAppointmentDTO.getId())
                    .patientId(patient.getId())
                    .patientEmail(patient.getEmail())
                    .patientPhone(patient.getPhoneNumber())
                    .doctorId(doctor.getId())
                    .doctorName("Dr. " + doctor.getFirstName() + " " + doctor.getLastName())
                    .appointmentDate(savedAppointmentDTO.getStartTime())
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of(
                            "type", savedAppointmentDTO.getType(),
                            "reason", savedAppointmentDTO.getReason()
                    ))
                    .build();
            
            log.info("📤 Sending event to Kafka topic: {}", appointmentEventsTopic);
            
            kafkaTemplate.send(appointmentEventsTopic, patient.getId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("✅ Kafka event published successfully. Topic: {}, Partition: {}, Offset: {}", 
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("❌ Failed to publish Kafka event: {}", ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("❌ Exception in Kafka publisher: {}", e.getMessage(), e);
        }
    }
}