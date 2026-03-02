package com.myheart.notification.dto;

import com.myheart.notification.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class NotificationRequestDTO {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    private String userEmail;
    
    private String userPhone;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    @NotNull(message = "Channel is required")
    private Notification.NotificationChannel channel;
    
    private String subject;
    
    private String content;
    
    private Map<String, Object> data;
    
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
}