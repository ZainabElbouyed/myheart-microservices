package com.myheart.appointment.service;

import com.myheart.common.dto.NotificationDTO;
import com.myheart.common.dto.PatientDTO;
import com.myheart.common.dto.DoctorDTO;
import com.myheart.appointment.client.PatientServiceClient;
import com.myheart.appointment.client.DoctorServiceClient;
import com.myheart.appointment.client.BillingServiceClient;
import com.myheart.common.constants.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.myheart.appointment.dto.*;
import com.myheart.appointment.entity.Appointment;
import com.myheart.appointment.exception.AppointmentNotFoundException;
import com.myheart.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    private final BillingServiceClient billingServiceClient;


    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        log.info("Creating appointment for patient: {}", request.getPatientId());
        
        // Récupérer les informations du patient
        PatientDTO patient = patientServiceClient.getPatientById(request.getPatientId());
        
        // Récupérer les informations du médecin
        DoctorDTO doctor = doctorServiceClient.getDoctorById(request.getDoctorId());
        
        // Vérifier la disponibilité
        Boolean available = doctorServiceClient.checkAvailability(
            doctor.getId(), request.getStartTime(), request.getEndTime());
        
        if (!available) {
            throw new RuntimeException("Doctor not available at this time");
        }
        
        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setPatientName(patient.getFirstName() + " " + patient.getLastName());
        appointment.setDoctorId(doctor.getId());
        appointment.setDoctorName("Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setType(request.getType());
        appointment.setReason(request.getReason());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created with ID: {}", savedAppointment.getId());
        
        // 1. 🔴 ENVOYER UNE NOTIFICATION AU PATIENT VIA RABBITMQ
        try {
            NotificationDTO notification = NotificationDTO.builder()
                    .userId(patient.getId())
                    .userEmail(patient.getEmail())
                    .type("APPOINTMENT_CONFIRMATION")
                    .channel("EMAIL")
                    .subject("Confirmation de votre rendez-vous")
                    .content(String.format(
                        "Votre rendez-vous avec Dr. %s %s est confirmé pour le %s",
                        doctor.getFirstName(), doctor.getLastName(), 
                        savedAppointment.getStartTime().toString()
                    ))
                    .data(Map.of(
                        "appointmentId", savedAppointment.getId(),
                        "doctorName", "Dr. " + doctor.getFirstName() + " " + doctor.getLastName(),
                        "date", savedAppointment.getStartTime().toString(),
                        "patientName", patient.getFirstName() + " " + patient.getLastName()
                    ))
                    .build();
            
            rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.NOTIFICATION_EMAIL_ROUTING_KEY,
                notification
            );
            log.info("📧 Notification email sent for appointment: {}", savedAppointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
        
        // 2. 🔴 CRÉER UNE FACTURE VIA BILLING-SERVICE (FEIGN)
        try {
            Map<String, Object> invoiceRequest = new HashMap<>();
            invoiceRequest.put("patientId", patient.getId());
            invoiceRequest.put("patientName", patient.getFirstName() + " " + patient.getLastName());
            invoiceRequest.put("appointmentId", savedAppointment.getId());
            invoiceRequest.put("subtotal", doctor.getConsultationFee() != null ? 
                doctor.getConsultationFee().doubleValue() : 50.0);
            invoiceRequest.put("taxRate", 20.0);
            invoiceRequest.put("description", "Consultation with " + doctor.getSpecialty());
            
            billingServiceClient.createInvoice(invoiceRequest);
            log.info("💰 Invoice created for appointment: {}", savedAppointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage());
        }
        
        // 3. 🔴 ÉMETTRE UN ÉVÉNEMENT DE RENDEZ-VOUS CRÉÉ
        try {
            Map<String, Object> appointmentEvent = new HashMap<>();
            appointmentEvent.put("appointmentId", savedAppointment.getId());
            appointmentEvent.put("patientId", patient.getId());
            appointmentEvent.put("doctorId", doctor.getId());
            appointmentEvent.put("date", savedAppointment.getStartTime().toString());
            
            rabbitTemplate.convertAndSend(
                "appointment.exchange",
                "appointment.created",
                appointmentEvent
            );
            log.info("📅 Appointment created event sent");
            
        } catch (Exception e) {
            log.error("Failed to send appointment event: {}", e.getMessage());
        }
        
        return AppointmentResponseDTO.fromEntity(savedAppointment);
    }
    
    public AppointmentResponseDTO getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        return AppointmentResponseDTO.fromEntity(appointment);
    }
    
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientIdOrderByStartTimeDesc(patientId)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorIdOrderByStartTimeDesc(doctorId)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getAppointmentsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
        
        return appointmentRepository.findByStartTimeBetween(start, end)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getUpcomingAppointmentsForPatient(String patientId) {
        List<Appointment.AppointmentStatus> excluded = List.of(
                Appointment.AppointmentStatus.COMPLETED,
                Appointment.AppointmentStatus.CANCELLED,
                Appointment.AppointmentStatus.NO_SHOW
        );
        
        return appointmentRepository
                .findByPatientIdAndStartTimeAfterAndStatusNotIn(patientId, LocalDateTime.now(), excluded)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getUpcomingAppointmentsForDoctor(String doctorId) {
        List<Appointment.AppointmentStatus> excluded = List.of(
                Appointment.AppointmentStatus.COMPLETED,
                Appointment.AppointmentStatus.CANCELLED,
                Appointment.AppointmentStatus.NO_SHOW
        );
        
        return appointmentRepository
                .findByDoctorIdAndStartTimeAfterAndStatusNotIn(doctorId, LocalDateTime.now(), excluded)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public AppointmentResponseDTO updateAppointment(String id, AppointmentUpdateDTO request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        // Si la date change, vérifier la disponibilité
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (!appointment.getStartTime().equals(request.getStartTime()) ||
                !appointment.getEndTime().equals(request.getEndTime())) {
                
                if (!isDoctorAvailable(appointment.getDoctorId(), request.getStartTime(), request.getEndTime())) {
                    throw new IllegalStateException("Doctor is not available at this time");
                }
                
                appointment.setStartTime(request.getStartTime());
                appointment.setEndTime(request.getEndTime());
                
                // Recalculer la durée
                int duration = (int) java.time.Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
                appointment.setDuration(duration);
                
                appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
            }
        }
        
        if (request.getType() != null) appointment.setType(request.getType());
        if (request.getReason() != null) appointment.setReason(request.getReason());
        if (request.getNotes() != null) appointment.setNotes(request.getNotes());
        if (request.getLocation() != null) appointment.setLocation(request.getLocation());
        if (request.getIsVirtual() != null) appointment.setIsVirtual(request.getIsVirtual());
        if (request.getMeetingLink() != null) appointment.setMeetingLink(request.getMeetingLink());
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }
    
    public AppointmentResponseDTO updateStatus(String id, AppointmentStatusUpdateDTO request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(request.getStatus());
        
        switch (request.getStatus()) {
            case CONFIRMED:
                appointment.setConfirmedAt(LocalDateTime.now());
                break;
            case CANCELLED:
                appointment.setCancelledAt(LocalDateTime.now());
                appointment.setCancellationReason(request.getCancellationReason());
                break;
            case COMPLETED:
                appointment.setCompletedAt(LocalDateTime.now());
                break;
            case CHECKED_IN:
                break;
            case IN_PROGRESS:
                break;
            case NO_SHOW:
                break;
            case RESCHEDULED:
                break;
            case SCHEDULED:
                break;
            default:
                break;
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }
    
    public AppointmentResponseDTO confirmAppointment(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointment.setConfirmedAt(LocalDateTime.now());
        
        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }
    
    public AppointmentResponseDTO cancelAppointment(String id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancellationReason(reason);
        
        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }
    
    public AppointmentResponseDTO completeAppointment(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(LocalDateTime.now());
        
        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }
    
    public AppointmentResponseDTO checkIn(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        
        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }
    
    public void deleteAppointment(String id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }
    
    public boolean isDoctorAvailable(String doctorId, LocalDateTime start, LocalDateTime end) {
        return !appointmentRepository.isDoctorAvailable(doctorId, start, end);
    }
    
    public List<AppointmentResponseDTO> getDoctorAppointmentsByDate(String doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getPatientAppointmentsByDate(String patientId, LocalDate date) {
        return appointmentRepository.findByPatientIdAndDate(patientId, date)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getTodayAppointments() {
        return appointmentRepository.findTodayAppointments()
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Map<String, Long> getStatusStatistics() {
        List<Object[]> results = appointmentRepository.countByStatus();
        Map<String, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            Appointment.AppointmentStatus status = (Appointment.AppointmentStatus) result[0];
            Long count = (Long) result[1];
            stats.put(status.name(), count);
        }
        
        return stats;
    }
    
    public Map<String, Object> getMonthlyStats(int year, int month) {
        List<Object[]> results = appointmentRepository.getMonthlyStats(year, month);
        Map<String, Object> stats = new HashMap<>();
        stats.put("year", year);
        stats.put("month", month);
        stats.put("total", results.stream().mapToLong(r -> (Long) r[1]).sum());
        
        Map<String, Long> byStatus = new HashMap<>();
        for (Object[] result : results) {
            Appointment.AppointmentStatus status = (Appointment.AppointmentStatus) result[0];
            Long count = (Long) result[1];
            byStatus.put(status.name(), count);
        }
        stats.put("byStatus", byStatus);
        
        return stats;
    }
}