package com.example.notification_service.mapper;


import com.example.notification_service.dto.NotificationRequestDto;
import com.example.notification_service.dto.NotificationResponseDto;
import com.example.notification_service.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Notification toEntity(NotificationRequestDto dto);

    NotificationResponseDto toDto(Notification entity);

}
