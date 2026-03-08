package com.myheart.lab.client.fallback;

import com.myheart.common.dto.NotificationRequest;
import com.myheart.lab.client.NotificationServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationServiceFallback implements NotificationServiceClient {
    
    @Override
    public void sendNotification(NotificationRequest request) {
        log.error("Fallback: notification-service indisponible - notification perdue: {}", request);
        // Sauvegarder en base pour reprise ultérieure
    }
}