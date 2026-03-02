// myheart-common/src/main/java/com/myheart/common/dto/NotificationDTO.java
package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String userId;
    private String userEmail;
    private String type;
    private String channel;
    private String subject;
    private String content;
    private Map<String, Object> data;
}