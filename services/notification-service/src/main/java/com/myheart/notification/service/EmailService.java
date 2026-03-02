package com.myheart.notification.service;

import com.myheart.notification.dto.EmailRequestDTO;
import com.myheart.notification.dto.NotificationRequestDTO;
import com.myheart.notification.entity.Notification;
import com.myheart.notification.exception.NotificationException;
import com.myheart.notification.repository.NotificationRepository;
import com.myheart.common.dto.NotificationDTO;           // ← NOUVEAU
import com.myheart.common.constants.RabbitMQConstants;  // ← NOUVEAU
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Async
    public void sendEmail(EmailRequestDTO request) {
        try {
            String content = request.getContent();
            
            // Utiliser un template si fourni
            if (request.getTemplate() != null && request.getTemplateData() != null) {
                content = processTemplate(request.getTemplate(), request.getTemplateData());
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(request.getFrom() != null ? request.getFrom() : fromEmail);
            helper.setTo(request.getTo());
            
            if (request.getCc() != null) {
                helper.setCc(request.getCc());
            }
            
            if (request.getBcc() != null) {
                helper.setBcc(request.getBcc());
            }
            
            if (request.getReplyTo() != null) {
                helper.setReplyTo(request.getReplyTo());
            }
            
            helper.setSubject(request.getSubject());
            helper.setText(content, true); // true = HTML
            
            mailSender.send(message);
            
            // Enregistrer la notification
            saveNotification(request, content);
            
            log.info("Email sent successfully to: {}", request.getTo());
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", request.getTo(), e);
            throw new NotificationException("Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendEmail(NotificationDTO notification) {
        log.info("📧 Sending email from NotificationDTO: {}", notification);
        
        EmailRequestDTO request = new EmailRequestDTO();
        request.setTo(notification.getUserEmail());
        request.setSubject(notification.getSubject());
        request.setUserId(notification.getUserId());
        request.setContent(notification.getContent());
        request.setTemplateData(notification.getData());
        
        // Déterminer le template selon le type
        switch (notification.getType()) {
            case "APPOINTMENT_CONFIRMATION":
                request.setTemplate("appointment-confirmation");
                break;
            case "LAB_RESULT_READY":
                request.setTemplate("lab-result-ready");
                break;
            case "PASSWORD_RESET":
                request.setTemplate("password-reset");
                break;
            default:
                // Pas de template spécifique
                break;
        }
        
        sendEmail(request);
    }
    
    private String processTemplate(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateName, context);
    }
    
    private void saveNotification(EmailRequestDTO request, String content) {
        if (request.getUserId() != null) {
            Notification notification = new Notification();
            notification.setUserId(request.getUserId());
            notification.setUserEmail(request.getTo());
            notification.setType(mapToNotificationType(request.getType()));
            notification.setChannel(Notification.NotificationChannel.EMAIL);
            notification.setSubject(request.getSubject());
            notification.setContent(content);
            notification.setData(request.getTemplateData());
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
            notificationRepository.save(notification);
        }
    }
    
    private Notification.NotificationType mapToNotificationType(NotificationRequestDTO.NotificationType type) {
        if (type == null) return Notification.NotificationType.SYSTEM_ALERT;
        
        return switch (type) {
            case APPOINTMENT_CONFIRMATION -> Notification.NotificationType.APPOINTMENT_CONFIRMATION;
            case APPOINTMENT_REMINDER -> Notification.NotificationType.APPOINTMENT_REMINDER;
            case APPOINTMENT_CANCELLATION -> Notification.NotificationType.APPOINTMENT_CANCELLATION;
            case LAB_RESULT_READY -> Notification.NotificationType.LAB_RESULT_READY;
            case PRESCRIPTION_READY -> Notification.NotificationType.PRESCRIPTION_READY;
            case BILLING_INVOICE -> Notification.NotificationType.BILLING_INVOICE;
            case PAYMENT_CONFIRMATION -> Notification.NotificationType.PAYMENT_CONFIRMATION;
            case PASSWORD_RESET -> Notification.NotificationType.PASSWORD_RESET;
            case ACCOUNT_VERIFICATION -> Notification.NotificationType.ACCOUNT_VERIFICATION;
            case WELCOME_MESSAGE -> Notification.NotificationType.WELCOME_MESSAGE;
            default -> Notification.NotificationType.SYSTEM_ALERT;
        };
    }
}