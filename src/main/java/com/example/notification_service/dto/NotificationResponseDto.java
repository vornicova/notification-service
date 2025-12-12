package com.example.notification_service.dto;

import com.example.notification_service.enums.NotificationChannel;
import com.example.notification_service.enums.NotificationStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class NotificationResponseDto {

    private UUID id;
    private NotificationChannel channel;
    private String recipient;
    private String subject;
    private String body;
    private NotificationStatus status;
    private String errorMessage;
    private OffsetDateTime createdAt;
    private OffsetDateTime sentAt;
    private Long customerId;

}