package com.myheart.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SmsRequestDTO {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String to;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private String userId;
    
    private NotificationRequestDTO.NotificationType type;
}