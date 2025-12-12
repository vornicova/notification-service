package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationRequestDto;
import com.example.notification_service.dto.NotificationResponseDto;
import com.example.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDto send(@Valid @RequestBody NotificationRequestDto requestDto) {
        return notificationService.sendNotification(requestDto);
    }

    @GetMapping("/{id}")
    public NotificationResponseDto getById(@PathVariable UUID id) {
        return notificationService.getNotification(id);
    }


    @GetMapping
    public List<NotificationResponseDto> getByCustomerId(
            @RequestParam("customerId") Long customerId
    ) {
        return notificationService.getNotificationsByCustomerId(customerId);
    }

}
