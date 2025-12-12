package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationRequestDto;
import com.example.notification_service.dto.NotificationResponseDto;

import java.util.List;
import java.util.UUID;

public interface

NotificationService {

    NotificationResponseDto sendNotification(NotificationRequestDto requestDto);

    NotificationResponseDto getNotification(UUID id);

    List<NotificationResponseDto> getNotificationsByCustomerId(Long customerId);

}