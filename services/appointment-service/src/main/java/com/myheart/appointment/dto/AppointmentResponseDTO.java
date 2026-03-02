package com.myheart.appointment.dto;

import com.myheart.appointment.entity.Appointment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponseDTO {
    
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Appointment.AppointmentStatus status;
    private Appointment.AppointmentType type;
    private String reason;
    private String notes;
    private String location;
    private Boolean isVirtual;
    private String meetingLink;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    
    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(appointment.getPatientName())
                .doctorId(appointment.getDoctorId())
                .doctorName(appointment.getDoctorName())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .location(appointment.getLocation())
                .isVirtual(appointment.getIsVirtual())
                .meetingLink(appointment.getMeetingLink())
                .duration(appointment.getDuration())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .confirmedAt(appointment.getConfirmedAt())
                .completedAt(appointment.getCompletedAt())
                .build();
    }
}