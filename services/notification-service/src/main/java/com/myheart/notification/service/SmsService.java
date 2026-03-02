package com.myheart.notification.service;

import com.myheart.notification.dto.NotificationRequestDTO;  
import com.myheart.notification.dto.SmsRequestDTO;
import com.myheart.notification.entity.Notification;
import com.myheart.notification.exception.NotificationException;
import com.myheart.notification.repository.NotificationRepository;
import com.myheart.common.dto.NotificationDTO;           // ← NOUVEAU
import com.myheart.common.constants.RabbitMQConstants;  // ← NOUVEAU
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {
    
    @Value("${twilio.account-sid}")
    private String accountSid;
    
    @Value("${twilio.auth-token}")
    private String authToken;
    
    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;
    
    private final NotificationRepository notificationRepository;
    
    @PostConstruct
    public void init() {
        if (accountSid != null && authToken != null) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio credentials not configured - SMS service will be simulated");
        }
    }
    
    @Async
    public void sendSms(SmsRequestDTO request) {
        try {
            String messageContent = request.getMessage();
            
            // Simuler ou envoyer réellement le SMS
            if (accountSid != null && authToken != null) {
                Message message = Message.creator(
                        new PhoneNumber(request.getTo()),
                        new PhoneNumber(fromPhoneNumber),
                        messageContent
                ).create();
                
                log.info("SMS sent successfully to: {}, SID: {}", request.getTo(), message.getSid());
            } else {
                // Mode simulation
                log.info("SIMULATED SMS - To: {}, Message: {}", request.getTo(), messageContent);
            }
            
            // Enregistrer la notification
            saveNotification(request, messageContent);
            
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", request.getTo(), e);
            throw new NotificationException("Failed to send SMS: " + e.getMessage());
        }
    }
    
    // 🔴 NOUVELLE MÉTHODE : sendSms à partir de NotificationDTO
    @Async
    public void sendSms(NotificationDTO notification) {
        log.info("📱 Sending SMS from NotificationDTO: {}", notification);
        
        try {
            // Extraire le numéro de téléphone (à adapter selon votre structure)
            // Vous pouvez ajouter un champ userPhone dans NotificationDTO si nécessaire
            String phoneNumber = extractPhoneNumber(notification);
            
            if (phoneNumber != null) {
                SmsRequestDTO request = new SmsRequestDTO();
                request.setTo(phoneNumber);
                request.setMessage(notification.getContent());
                request.setUserId(notification.getUserId());
                request.setType(mapToRequestType(notification.getType()));
                
                sendSms(request);
            } else {
                log.warn("No phone number found for user: {}", notification.getUserId());
            }
            
        } catch (Exception e) {
            log.error("Failed to send SMS from notification: {}", e.getMessage());
        }
    }
    
    // 🔴 MÉTHODE UTILITAIRE pour extraire le numéro de téléphone
    private String extractPhoneNumber(NotificationDTO notification) {
        // Cette méthode dépend de comment vous stockez les numéros
        // Vous pouvez ajouter un champ userPhone dans NotificationDTO
        // ou le récupérer depuis les données
        if (notification.getData() != null && notification.getData().containsKey("phoneNumber")) {
            return (String) notification.getData().get("phoneNumber");
        }
        return null;
    }
    
    // 🔴 MÉTHODE UTILITAIRE pour convertir le type
    private NotificationRequestDTO.NotificationType mapToRequestType(String type) {
        try {
            return NotificationRequestDTO.NotificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return NotificationRequestDTO.NotificationType.SYSTEM_ALERT;
        }
    }
    
    private void saveNotification(SmsRequestDTO request, String content) {
        if (request.getUserId() != null) {
            Notification notification = new Notification();
            notification.setUserId(request.getUserId());
            notification.setUserPhone(request.getTo());
            notification.setType(mapToNotificationType(request.getType()));
            notification.setChannel(Notification.NotificationChannel.SMS);
            notification.setContent(content);
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
            notificationRepository.save(notification);
        }
    }
    
    private Notification.NotificationType mapToNotificationType(NotificationRequestDTO.NotificationType type) {
        if (type == null) return Notification.NotificationType.SYSTEM_ALERT;
        
        return switch (type) {
            case APPOINTMENT_CONFIRMATION -> Notification.NotificationType.APPOINTMENT_CONFIRMATION;
            case APPOINTMENT_REMINDER -> Notification.NotificationType.APPOINTMENT_REMINDER;
            case APPOINTMENT_CANCELLATION -> Notification.NotificationType.APPOINTMENT_CANCELLATION;
            case LAB_RESULT_READY -> Notification.NotificationType.LAB_RESULT_READY;
            case PRESCRIPTION_READY -> Notification.NotificationType.PRESCRIPTION_READY;
            case BILLING_INVOICE -> Notification.NotificationType.BILLING_INVOICE;
            case PAYMENT_CONFIRMATION -> Notification.NotificationType.PAYMENT_CONFIRMATION;
            case PASSWORD_RESET -> Notification.NotificationType.PASSWORD_RESET;
            case ACCOUNT_VERIFICATION -> Notification.NotificationType.ACCOUNT_VERIFICATION;
            case WELCOME_MESSAGE -> Notification.NotificationType.WELCOME_MESSAGE;
            default -> Notification.NotificationType.SYSTEM_ALERT;
        };
    }
}