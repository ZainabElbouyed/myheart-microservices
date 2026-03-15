package com.myheart.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String eventId;
    private String type; // EMAIL, SMS, PUSH
    private String recipient;
    private String subject;
    private String content;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
}