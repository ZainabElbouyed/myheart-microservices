// services/notification-service/src/main/java/com/myheart/notification/consumer/NotificationConsumer.java
package com.myheart.notification.consumer;

import com.myheart.common.dto.NotificationDTO;
import com.myheart.common.constants.RabbitMQConstants;
import com.myheart.notification.entity.Notification;
import com.myheart.notification.repository.NotificationRepository;
import com.myheart.notification.service.EmailService;
import com.myheart.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    
    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationRepository notificationRepository;
    
    @RabbitListener(queues = RabbitMQConstants.EMAIL_QUEUE)
    public void handleEmailNotification(NotificationDTO notification) {
        log.info("📧 Email notification received: {}", notification);
        
        try {
            emailService.sendEmail(notification);
            
            // 🔴 CORRECTION: Convertir les String en enums
            Notification entity = new Notification();
            entity.setUserId(notification.getUserId());
            entity.setUserEmail(notification.getUserEmail());
            entity.setType(mapToNotificationType(notification.getType()));
            entity.setChannel(Notification.NotificationChannel.EMAIL);
            entity.setSubject(notification.getSubject());
            entity.setContent(notification.getContent());
            entity.setData(notification.getData());
            entity.setStatus(Notification.NotificationStatus.SENT);
            entity.setSentAt(java.time.LocalDateTime.now());
            
            notificationRepository.save(entity);
            
            log.info("✅ Email sent to: {}", notification.getUserEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
        }
    }
    
    @RabbitListener(queues = RabbitMQConstants.SMS_QUEUE)
    public void handleSmsNotification(NotificationDTO notification) {
        log.info("📱 SMS notification received: {}", notification);
        
        try {
            smsService.sendSms(notification);
            
            // 🔴 CORRECTION: Convertir les String en enums
            Notification entity = new Notification();
            entity.setUserId(notification.getUserId());
            entity.setUserPhone(extractPhoneNumber(notification));
            entity.setType(mapToNotificationType(notification.getType()));
            entity.setChannel(Notification.NotificationChannel.SMS);
            entity.setContent(notification.getContent());
            entity.setStatus(Notification.NotificationStatus.SENT);
            entity.setSentAt(java.time.LocalDateTime.now());
            
            notificationRepository.save(entity);
            
            log.info("✅ SMS sent to: {}", notification.getUserId());
        } catch (Exception e) {
            log.error("❌ Failed to send SMS: {}", e.getMessage());
        }
    }
    
    @RabbitListener(queues = RabbitMQConstants.PUSH_QUEUE)
    public void handlePushNotification(NotificationDTO notification) {
        log.info("🔔 Push notification received: {}", notification);
        // Implémenter l'envoi de notifications push
    }
    
    // 🔴 MÉTHODE UTILITAIRE: Convertir String en enum NotificationType
    private Notification.NotificationType mapToNotificationType(String type) {
        if (type == null) return Notification.NotificationType.SYSTEM_ALERT;
        
        try {
            return Notification.NotificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown notification type: {}, using SYSTEM_ALERT", type);
            return Notification.NotificationType.SYSTEM_ALERT;
        }
    }
    
    // 🔴 MÉTHODE UTILITAIRE: Extraire le numéro de téléphone
    private String extractPhoneNumber(NotificationDTO notification) {
        if (notification.getData() != null && notification.getData().containsKey("phoneNumber")) {
            return (String) notification.getData().get("phoneNumber");
        }
        return null;
    }
}