package com.myheart.notification.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
public class Notification {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String userEmail;
    
    private String userPhone;
    
    @Indexed
    private NotificationType type;
    
    private NotificationChannel channel;
    
    private String subject;
    
    private String content;
    
    private Map<String, Object> data;
    
    @Indexed
    private NotificationStatus status = NotificationStatus.PENDING;
    
    private Integer retryCount = 0;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime deliveredAt;
    
    private LocalDateTime readAt;
    
    private String errorMessage;
    
    private String providerResponse;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        APPOINTMENT_CONFIRMATION,
        APPOINTMENT_REMINDER,
        APPOINTMENT_CANCELLATION,
        LAB_RESULT_READY,
        PRESCRIPTION_READY,
        BILLING_INVOICE,
        PAYMENT_CONFIRMATION,
        PASSWORD_RESET,
        ACCOUNT_VERIFICATION,
        WELCOME_MESSAGE,
        SYSTEM_ALERT
    }
    
    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        IN_APP
    }
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        READ,
        FAILED,
        CANCELLED
    }
}