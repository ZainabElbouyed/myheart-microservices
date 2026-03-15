package com.myheart.appointment.service;

import com.myheart.common.dto.NotificationDTO;
import com.myheart.common.dto.PatientDTO;
import com.myheart.common.dto.DoctorDTO;
import com.myheart.common.dto.InvoiceRequest;
import com.myheart.appointment.client.PatientServiceClient;
import com.myheart.appointment.client.DoctorServiceClient;
import com.myheart.appointment.client.BillingServiceClient;
import com.myheart.common.constants.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.myheart.appointment.dto.*;
import com.myheart.appointment.entity.Appointment;
import com.myheart.appointment.exception.AppointmentNotFoundException;
import com.myheart.appointment.repository.AppointmentRepository;
import com.myheart.appointment.service.AppointmentEventPublisher; 
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    
    // ========== CLIENTS FEIGN ==========
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    private final BillingServiceClient billingServiceClient;

    private final AppointmentEventPublisher eventPublisher; 

    // ========== MÉTHODE PRINCIPALE ==========
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        log.info("Creating appointment for patient: {}", request.getPatientId());

        PatientDTO patient = getPatient(request.getPatientId());
        DoctorDTO doctor = getDoctor(request.getDoctorId());
        
        Boolean available = true;

        try {
            available = doctorServiceClient.checkAvailability(
                    doctor.getId(),
                    request.getStartTime(),
                    request.getEndTime()
            );
        } catch (Exception e) {
            log.warn("Doctor availability check failed: {}", e.getMessage());
        }

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

        
        // Publier l'événement Kafka pour notification-service
        eventPublisher.publishAppointmentCreated(AppointmentResponseDTO.fromEntity(savedAppointment), patient, doctor);

        sendNotification(patient, doctor, savedAppointment);
        createInvoice(patient, doctor, savedAppointment);
        publishAppointmentEvent(patient, doctor, savedAppointment);
        
        return AppointmentResponseDTO.fromEntity(savedAppointment);
    }
    
    // ========== FALLBACK ==========

    @CircuitBreaker(name = "patientService", fallbackMethod = "getPatientFallback")
    public PatientDTO getPatient(String patientId) {
        return patientServiceClient.getPatientById(patientId);
    }

    public PatientDTO getPatientFallback(String patientId, Exception e) {
        log.error("Patient service unavailable: {}", e.getMessage());

        return PatientDTO.builder()
                .id(patientId)
                .firstName("Patient")
                .lastName("Indisponible")
                .email("fallback@system.com")
                .build();
    }

    @CircuitBreaker(name = "doctorService", fallbackMethod = "getDoctorFallback")
    public DoctorDTO getDoctor(String doctorId) {
        return doctorServiceClient.getDoctorById(doctorId);
    }

    public DoctorDTO getDoctorFallback(String doctorId, Exception e) {
        log.error("Doctor service unavailable: {}", e.getMessage());

        return DoctorDTO.builder()
                .id(doctorId)
                .firstName("Médecin")
                .lastName("Indisponible")
                .build();
    }
    
        // ========== MÉTHODES MANQUANTES POUR LE CONTROLLER ==========
    
    /**
     * Récupère les rendez-vous d'un docteur pour une date spécifique
     */
    public List<AppointmentResponseDTO> getDoctorAppointmentsByDate(String doctorId, LocalDate date) {
        log.info("Récupération des rendez-vous du docteur {} pour la date {}", doctorId, date);
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les rendez-vous d'un patient pour une date spécifique
     */
    public List<AppointmentResponseDTO> getPatientAppointmentsByDate(String patientId, LocalDate date) {
        log.info("Récupération des rendez-vous du patient {} pour la date {}", patientId, date);
        return appointmentRepository.findByPatientIdAndDate(patientId, date)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les rendez-vous du jour
     */
    public List<AppointmentResponseDTO> getTodayAppointments() {
        log.info("Récupération des rendez-vous du jour");
        return appointmentRepository.findTodayAppointments()
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Met à jour le statut d'un rendez-vous
     */
    public AppointmentResponseDTO updateStatus(String id, AppointmentStatusUpdateDTO request) {
        log.info("Mise à jour du statut du rendez-vous {} vers {}", id, request.getStatus());
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(request.getStatus());
        
        // Gestion des timestamps selon le statut
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
                // Pas de timestamp spécifique
                break;
            case IN_PROGRESS:
                // Pas de timestamp spécifique
                break;
            case NO_SHOW:
                // Pas de timestamp spécifique
                break;
            case RESCHEDULED:
                // Pas de timestamp spécifique
                break;
            case SCHEDULED:
                // Pas de timestamp spécifique
                break;
            default:
                break;
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }
    
    /**
     * Enregistre l'arrivée du patient (check-in)
     */
    public AppointmentResponseDTO checkIn(String id) {
        log.info("Check-in pour le rendez-vous {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }
    
    /**
     * Récupère les statistiques par statut
     */
    public Map<String, Long> getStatusStatistics() {
        log.info("Récupération des statistiques par statut");
        
        List<Object[]> results = appointmentRepository.countByStatus();
        Map<String, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            Appointment.AppointmentStatus status = (Appointment.AppointmentStatus) result[0];
            Long count = (Long) result[1];
            stats.put(status.name(), count);
        }
        
        return stats;
    }
    
    /**
     * Récupère les statistiques mensuelles
     */
    public Map<String, Object> getMonthlyStats(int year, int month) {
        log.info("Récupération des statistiques pour {}-{}", year, month);
        
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
    // ========== MÉTHODES PRIVÉES ==========
    
    private void sendNotification(PatientDTO patient, DoctorDTO doctor, Appointment appointment) {
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
                        appointment.getStartTime().toString()
                    ))
                    .data(Map.of(
                        "appointmentId", appointment.getId(),
                        "doctorName", "Dr. " + doctor.getFirstName() + " " + doctor.getLastName(),
                        "date", appointment.getStartTime().toString(),
                        "patientName", patient.getFirstName() + " " + patient.getLastName()
                    ))
                    .build();
            
            rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.NOTIFICATION_EMAIL_ROUTING_KEY,
                notification
            );
            log.info("📧 Notification email sent for appointment: {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
    
    @CircuitBreaker(name = "billingService", fallbackMethod = "createInvoiceFallback")
    private void createInvoice(PatientDTO patient, DoctorDTO doctor, Appointment appointment) {
        try {
            InvoiceRequest invoiceRequest = new InvoiceRequest();
            invoiceRequest.setPatientId(patient.getId());
            invoiceRequest.setPatientName(patient.getFirstName() + " " + patient.getLastName());
            invoiceRequest.setAppointmentId(appointment.getId());
            
            // ✅ CORRECTION: Conversion de BigDecimal à Double
            BigDecimal consultationFee = doctor.getConsultationFee();
            if (consultationFee != null) {
                invoiceRequest.setSubtotal(consultationFee.doubleValue());  // Conversion explicite
            } else {
                invoiceRequest.setSubtotal(50.0);  // Valeur par défaut
            }
            
            invoiceRequest.setTaxRate(20.0);
            invoiceRequest.setDescription("Consultation with " + doctor.getSpecialty());
            
            billingServiceClient.createInvoice(invoiceRequest);
            log.info("💰 Invoice created for appointment: {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage());
            throw e; // Pour déclencher le circuit breaker
        }
    }
    
    /**
     * Fallback pour createInvoice
     */
    private void createInvoiceFallback(PatientDTO patient, DoctorDTO doctor, Appointment appointment, Exception e) {
        log.error("Fallback pour createInvoice - billing-service indisponible: {}", e.getMessage());
        // La facture sera créée plus tard (par un job de reprise)
    }
    
    private void publishAppointmentEvent(PatientDTO patient, DoctorDTO doctor, Appointment appointment) {
        try {
            Map<String, Object> appointmentEvent = new HashMap<>();
            appointmentEvent.put("appointmentId", appointment.getId());
            appointmentEvent.put("patientId", patient.getId());
            appointmentEvent.put("doctorId", doctor.getId());
            appointmentEvent.put("date", appointment.getStartTime().toString());
            
            rabbitTemplate.convertAndSend(
                "appointment.exchange",
                "appointment.created",
                appointmentEvent
            );
            log.info("📅 Appointment created event sent");
            
        } catch (Exception e) {
            log.error("Failed to send appointment event: {}", e.getMessage());
        }
    }
    
    private com.myheart.common.dto.AppointmentDTO convertToCommonDTO(Appointment appointment) {
        return com.myheart.common.dto.AppointmentDTO.builder()
            .id(appointment.getId())
            .patientId(appointment.getPatientId())
            .patientName(appointment.getPatientName())
            .doctorId(appointment.getDoctorId())
            .doctorName(appointment.getDoctorName())
            .startTime(appointment.getStartTime())
            .endTime(appointment.getEndTime())
            .status(appointment.getStatus().toString())
            .type(appointment.getType() != null ? appointment.getType().toString() : null)
            .reason(appointment.getReason())
            .build();
    }
    // ========== MÉTHODES EXISTANTES ==========
    
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
        
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (!appointment.getStartTime().equals(request.getStartTime()) ||
                !appointment.getEndTime().equals(request.getEndTime())) {
                
                if (!isDoctorAvailable(appointment.getDoctorId(), request.getStartTime(), request.getEndTime())) {
                    throw new IllegalStateException("Doctor is not available at this time");
                }
                
                appointment.setStartTime(request.getStartTime());
                appointment.setEndTime(request.getEndTime());
                
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
    
    public void deleteAppointment(String id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }
    
    public boolean isDoctorAvailable(String doctorId, LocalDateTime start, LocalDateTime end) {
        return !appointmentRepository.isDoctorAvailable(doctorId, start, end);
    }

    
}
