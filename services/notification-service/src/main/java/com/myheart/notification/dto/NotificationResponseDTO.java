package com.myheart.notification.dto;

import com.myheart.notification.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    
    private String id;
    private String userId;
    private String userEmail;
    private String userPhone;
    private Notification.NotificationType type;
    private Notification.NotificationChannel channel;
    private String subject;
    private String content;
    private Notification.NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    
    public static NotificationResponseDTO fromEntity(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .userEmail(notification.getUserEmail())
                .userPhone(notification.getUserPhone())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .deliveredAt(notification.getDeliveredAt())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}