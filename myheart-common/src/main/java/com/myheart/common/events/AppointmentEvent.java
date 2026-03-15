package com.myheart.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEvent {
    private String eventId;
    private String eventType; // CREATED, UPDATED, CANCELLED, COMPLETED
    private String appointmentId;
    private String patientId;
    private String patientEmail;
    private String patientPhone;
    private String doctorId;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}