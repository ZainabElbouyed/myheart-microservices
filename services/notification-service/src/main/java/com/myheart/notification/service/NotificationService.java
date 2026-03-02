// services/notification-service/src/main/java/com/myheart/notification/service/NotificationService.java
package com.myheart.notification.service;

import com.myheart.common.constants.RabbitMQConstants;
import com.myheart.notification.dto.NotificationRequestDTO;
import com.myheart.notification.dto.NotificationResponseDTO;
import com.myheart.notification.entity.Notification;
import com.myheart.notification.exception.NotificationException;
import com.myheart.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final EmailService emailService;
    private final SmsService smsService;
    
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {
        log.info("Sending notification to user: {}, type: {}, channel: {}", 
                request.getUserId(), request.getType(), request.getChannel());
        
        // Créer et sauvegarder la notification
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setUserEmail(request.getUserEmail());
        notification.setUserPhone(request.getUserPhone());
        notification.setType(mapToEntityType(request.getType()));
        notification.setChannel(request.getChannel());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setData(request.getData());
        notification.setStatus(Notification.NotificationStatus.PENDING);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Envoyer à la file d'attente appropriée
        String routingKey = getRoutingKey(request.getChannel());
        // 🔴 CORRECTION: Utiliser RabbitMQConstants
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, routingKey, savedNotification);
        
        log.info("Notification queued with ID: {}", savedNotification.getId());
        
        return NotificationResponseDTO.fromEntity(savedNotification);
    }
    
    public NotificationResponseDTO getNotificationById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException("Notification not found with id: " + id));
        return NotificationResponseDTO.fromEntity(notification);
    }
    
    public List<NotificationResponseDTO> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<NotificationResponseDTO> getUnreadNotifications(String userId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return notificationRepository.findUnreadByUserSince(userId, oneMonthAgo)
                .stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndStatus(userId, Notification.NotificationStatus.SENT);
    }
    
    public NotificationResponseDTO markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException("Notification not found with id: " + id));
        
        notification.setStatus(Notification.NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        
        Notification updatedNotification = notificationRepository.save(notification);
        return NotificationResponseDTO.fromEntity(updatedNotification);
    }
    
    public NotificationResponseDTO markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndStatus(userId, Notification.NotificationStatus.SENT);
        
        unreadNotifications.forEach(n -> {
            n.setStatus(Notification.NotificationStatus.READ);
            n.setReadAt(LocalDateTime.now());
        });
        
        notificationRepository.saveAll(unreadNotifications);
        
        return NotificationResponseDTO.builder()
                .userId(userId)
                .status(Notification.NotificationStatus.READ)
                .build();
    }
    
    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new NotificationException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }
    
    public List<NotificationResponseDTO> getPendingNotifications() {
        return notificationRepository.findPendingNotifications()
                .stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    private Notification.NotificationType mapToEntityType(NotificationRequestDTO.NotificationType type) {
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
    
    // 🔴 CORRECTION: Utiliser les constantes de RabbitMQConstants
    private String getRoutingKey(Notification.NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> RabbitMQConstants.NOTIFICATION_EMAIL_ROUTING_KEY;
            case SMS -> RabbitMQConstants.NOTIFICATION_SMS_ROUTING_KEY;
            case PUSH -> RabbitMQConstants.NOTIFICATION_PUSH_ROUTING_KEY;
            default -> throw new IllegalArgumentException("Unsupported channel: " + channel);
        };
    }
}