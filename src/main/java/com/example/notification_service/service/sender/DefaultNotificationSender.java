package com.example.notification_service.service.sender;

import com.example.notification_service.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info(
                "📨 MOCK NOTIFICATION | customerId={} | subject='{}' | body='{}'",
                notification.getCustomerId(),
                notification.getSubject(),
                notification.getBody()
        );
    }
}
