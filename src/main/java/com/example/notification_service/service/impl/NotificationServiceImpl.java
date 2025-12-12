package com.example.notification_service.service.impl;

import com.example.notification_service.dto.NotificationRequestDto;
import com.example.notification_service.dto.NotificationResponseDto;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.enums.NotificationStatus;
import com.example.notification_service.exception.NotFoundException;
import com.example.notification_service.mapper.NotificationMapper;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.service.NotificationService;
import com.example.notification_service.service.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final NotificationSender sender;

    @Override
    public NotificationResponseDto sendNotification(NotificationRequestDto requestDto) {
        Notification notification = mapper.toEntity(requestDto);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(OffsetDateTime.now());

        notification = repository.save(notification);

        try {
            sender.send(notification);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(OffsetDateTime.now());
        } catch (Exception ex) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(ex.getMessage());
        }

        notification = repository.save(notification);
        return mapper.toDto(notification);
    }

    @Override
    public NotificationResponseDto getNotification(UUID id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification with id " + id + " not found"));
        return mapper.toDto(notification);
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByCustomerId(Long customerId) {
        //дописать не забыть notificationRepository.findByCustomerId(customerId).
        return List.of();
    }
}
