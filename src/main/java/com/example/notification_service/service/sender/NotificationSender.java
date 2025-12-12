package com.example.notification_service.service.sender;

import com.example.notification_service.entity.Notification;

public interface NotificationSender {
    void send(Notification notification);
}
