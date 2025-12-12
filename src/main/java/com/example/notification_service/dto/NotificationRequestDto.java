package com.example.notification_service.dto;

import com.example.notification_service.enums.NotificationChannel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDto {

    @NotNull(message = "Channel is required")
    private NotificationChannel channel; // пока фактически используем EMAIL

    @NotBlank(message = "Recipient is required")
    @Email(message = "Recipient must be a valid email")
    private String recipient;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;
    private Long customerId;

}
