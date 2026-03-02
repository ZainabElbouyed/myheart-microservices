package com.myheart.appointment.dto;

import com.myheart.appointment.entity.Appointment;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentUpdateDTO {
    
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    private Appointment.AppointmentType type;
    private String reason;
    private String notes;
    private String location;
    private Boolean isVirtual;
    private String meetingLink;
}