package com.myheart.notification.consumer;

import com.myheart.common.events.AppointmentEvent;
import com.myheart.common.dto.NotificationDTO;
import com.myheart.notification.service.EmailService;
import com.myheart.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {
    
    private final EmailService emailService;
    private final SmsService smsService;
    
    @KafkaListener(topics = "${spring.kafka.topics.appointment-events}", 
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAppointmentEvent(AppointmentEvent event) {
        log.info("📥 Received appointment event: {} - {}", event.getEventType(), event.getEventId());
        
        switch (event.getEventType()) {
            case "CREATED":
                handleAppointmentCreated(event);
                break;
            case "CANCELLED":
                handleAppointmentCancelled(event);
                break;
            case "UPDATED":
                handleAppointmentUpdated(event);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
    
    private void handleAppointmentCreated(AppointmentEvent event) {
        // Créer un NotificationDTO à partir de l'événement
        NotificationDTO notification = NotificationDTO.builder()
                .userId(event.getPatientId())
                .userEmail(event.getPatientEmail())
                .type(event.getEventType())
                .channel("EMAIL")
                .subject("Confirmation de votre rendez-vous")
                .content(String.format(
                    "Bonjour,\n\nVotre rendez-vous avec %s est confirmé pour le %s.",
                    event.getDoctorName(), event.getAppointmentDate()
                ))
                .data(Map.of(
                    "appointmentId", event.getAppointmentId(),
                    "doctorName", event.getDoctorName(),
                    "appointmentDate", event.getAppointmentDate().toString(),
                    "eventId", event.getEventId()
                ))
                .build();
        
        // Envoyer email en utilisant la méthode qui accepte NotificationDTO
        if (event.getPatientEmail() != null) {
            emailService.sendEmail(notification);  // ← Utilise la méthode avec NotificationDTO
            log.info("📧 Email sent to patient: {}", event.getPatientEmail());
        }
        
        // Créer une notification SMS
        if (event.getPatientPhone() != null) {
            NotificationDTO smsNotification = NotificationDTO.builder()
                    .userId(event.getPatientId())
                    .type(event.getEventType())
                    .channel("SMS")
                    .content(String.format(
                        "Rappel: Rendez-vous avec %s le %s",
                        event.getDoctorName(), event.getAppointmentDate()
                    ))
                    .data(Map.of(
                        "phoneNumber", event.getPatientPhone(),
                        "appointmentId", event.getAppointmentId()
                    ))
                    .build();
            
            smsService.sendSms(smsNotification);  // ← Utilise la méthode avec NotificationDTO
            log.info("📱 SMS sent to patient: {}", event.getPatientPhone());
        }
    }
    
    private void handleAppointmentCancelled(AppointmentEvent event) {
        log.info("Appointment cancelled: {}", event.getAppointmentId());
        
        if (event.getPatientEmail() != null) {
            NotificationDTO notification = NotificationDTO.builder()
                    .userId(event.getPatientId())
                    .userEmail(event.getPatientEmail())
                    .type("CANCELLED")
                    .channel("EMAIL")
                    .subject("Annulation de votre rendez-vous")
                    .content(String.format(
                        "Bonjour,\n\nVotre rendez-vous avec %s prévu le %s a été annulé.",
                        event.getDoctorName(), event.getAppointmentDate()
                    ))
                    .data(Map.of("appointmentId", event.getAppointmentId()))
                    .build();
            
            emailService.sendEmail(notification);
        }
    }
    
    private void handleAppointmentUpdated(AppointmentEvent event) {
        log.info("Appointment updated: {}", event.getAppointmentId());
        
        if (event.getPatientEmail() != null) {
            NotificationDTO notification = NotificationDTO.builder()
                    .userId(event.getPatientId())
                    .userEmail(event.getPatientEmail())
                    .type("UPDATED")
                    .channel("EMAIL")
                    .subject("Modification de votre rendez-vous")
                    .content(String.format(
                        "Bonjour,\n\nVotre rendez-vous avec %s a été modifié. Nouvelle date: %s.",
                        event.getDoctorName(), event.getAppointmentDate()
                    ))
                    .data(Map.of("appointmentId", event.getAppointmentId()))
                    .build();
            
            emailService.sendEmail(notification);
        }
    }
}