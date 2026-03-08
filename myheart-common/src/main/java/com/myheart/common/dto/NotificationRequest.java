package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String patientId;
    private String doctorId;
    private String type;           // APPOINTMENT_CONFIRMATION, LAB_RESULT_READY, etc.
    private String channel;         // EMAIL, SMS, PUSH
    private String subject;
    private String content;
    private String recipientEmail;
    private String recipientPhone;
    private Map<String, Object> data;
    private String templateName;
    private Map<String, Object> templateParams;
}