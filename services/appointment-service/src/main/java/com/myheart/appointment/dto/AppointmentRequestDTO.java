package com.myheart.appointment.dto;

import com.myheart.appointment.entity.Appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequestDTO {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    private String patientName;
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    private String doctorName;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    private Appointment.AppointmentType type;
    private String reason;
    private String notes;
    private String location;
    private Boolean isVirtual = false;
    private String meetingLink;
}