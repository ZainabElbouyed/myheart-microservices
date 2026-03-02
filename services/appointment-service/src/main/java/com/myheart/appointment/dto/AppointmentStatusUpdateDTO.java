package com.myheart.appointment.dto;

import com.myheart.appointment.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentStatusUpdateDTO {
    
    @NotNull(message = "Status is required")
    private Appointment.AppointmentStatus status;
    
    private String cancellationReason;
}
