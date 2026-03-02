package com.myheart.appointment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String patientId;
    
    private String patientName;
    
    @Column(nullable = false)
    private String doctorId;
    
    private String doctorName;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;
    
    @Enumerated(EnumType.STRING)
    private AppointmentType type;
    
    private String reason;
    
    private String notes;
    
    private String location;
    
    private Boolean isVirtual = false;
    
    private String meetingLink;
    
    private Integer duration;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private LocalDateTime confirmedAt;
    
    private LocalDateTime cancelledAt;
    
    private String cancellationReason;
    
    private LocalDateTime completedAt;
    
    // Rendre les enums PUBLIQUES et STATIQUES
    public enum AppointmentStatus {
        SCHEDULED, CONFIRMED, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW, RESCHEDULED
    }
    
    public enum AppointmentType {
        CONSULTATION, FOLLOW_UP, EMERGENCY, CHECKUP, PROCEDURE, TELEMEDICINE
    }
}