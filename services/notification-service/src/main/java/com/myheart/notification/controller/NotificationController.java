package com.myheart.notification.controller;

import com.myheart.notification.dto.EmailRequestDTO;
import com.myheart.notification.dto.NotificationRequestDTO;
import com.myheart.notification.dto.NotificationResponseDTO;
import com.myheart.notification.dto.SmsRequestDTO;
import com.myheart.notification.service.EmailService;
import com.myheart.notification.service.NotificationService;
import com.myheart.notification.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SmsService smsService;
    
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> sendNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        emailService.sendEmail(request);
        return ResponseEntity.accepted().build();
    }
    
    @PostMapping("/sms")
    public ResponseEntity<Void> sendSms(@Valid @RequestBody SmsRequestDTO request) {
        smsService.sendSms(request);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable String id) {
        NotificationResponseDTO response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByUser(@PathVariable String userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable String userId) {
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable String id) {
        NotificationResponseDTO response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/user/{userId}/read-all")
    public ResponseEntity<NotificationResponseDTO> markAllAsRead(@PathVariable String userId) {
        NotificationResponseDTO response = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<NotificationResponseDTO>> getPendingNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getPendingNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification service is running");
    }
}