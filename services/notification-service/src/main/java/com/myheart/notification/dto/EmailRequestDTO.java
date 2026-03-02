package com.myheart.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class EmailRequestDTO {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;
    
    private String[] cc;
    
    private String[] bcc;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String template;
    
    private Map<String, Object> templateData;
    
    private String from;
    
    private String replyTo;
    
    private String userId;
    
    private NotificationRequestDTO.NotificationType type;
}