package com.myheart.lab.client;

import com.myheart.common.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "notification-service",
    path = "/api/notifications",
    contextId = "labNotificationClient",
    fallback = com.myheart.lab.client.fallback.NotificationServiceFallback.class
)
public interface NotificationServiceClient {
    
    @PostMapping("/send")
    void sendNotification(@RequestBody NotificationRequest request);
}