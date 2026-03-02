package com.myheart.notification.repository;

import com.myheart.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<Notification> findByUserIdAndStatus(String userId, Notification.NotificationStatus status);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    @Query("{ 'userId': ?0, 'status': { $ne: 'READ' }, 'createdAt': { $gte: ?1 } }")
    List<Notification> findUnreadByUserSince(String userId, LocalDateTime since);
    
    @Query("{ 'status': 'PENDING', 'retryCount': { $lt: 3 } }")
    List<Notification> findPendingNotifications();
    
    @Query("{ 'createdAt': { $lt: ?0 }, 'status': 'PENDING' }")
    List<Notification> findStuckNotifications(LocalDateTime threshold);
    
    long countByUserIdAndStatus(String userId, Notification.NotificationStatus status);
    
    long countByTypeAndCreatedAtBetween(Notification.NotificationType type, LocalDateTime start, LocalDateTime end);
}