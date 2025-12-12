package com.example.notification_service.service.impl;

import com.example.notification_service.dto.NotificationRequestDto;
import com.example.notification_service.dto.NotificationResponseDto;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.enums.NotificationStatus;
import com.example.notification_service.exception.NotFoundException;
import com.example.notification_service.mapper.NotificationMapper;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.service.sender.DefaultNotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private DefaultNotificationSender sender;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void sendNotification_shouldSavePendingThenSent_whenSenderSucceeds() {
        // given
        NotificationRequestDto requestDto = new NotificationRequestDto();
        // можно выставить какие-то поля, если нужно

        Notification entityBeforeSave = new Notification();
        entityBeforeSave.setStatus(null);

        Notification entityAfterFirstSave = new Notification();
        entityAfterFirstSave.setId(UUID.randomUUID());
        entityAfterFirstSave.setStatus(NotificationStatus.PENDING);

        Notification entityAfterSecondSave = new Notification();
        entityAfterSecondSave.setId(entityAfterFirstSave.getId());
        entityAfterSecondSave.setStatus(NotificationStatus.SENT);
        entityAfterSecondSave.setCreatedAt(OffsetDateTime.now().minusSeconds(1));
        entityAfterSecondSave.setSentAt(OffsetDateTime.now());

        NotificationResponseDto responseDto = new NotificationResponseDto();
        responseDto.setId(entityAfterSecondSave.getId());
        responseDto.setStatus(NotificationStatus.SENT);

        when(mapper.toEntity(requestDto)).thenReturn(entityBeforeSave);
        when(repository.save(any(Notification.class)))
                .thenReturn(entityAfterFirstSave)   // первый save (PENDING)
                .thenReturn(entityAfterSecondSave); // второй save (SENT)
        when(mapper.toDto(entityAfterSecondSave)).thenReturn(responseDto);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // when
        NotificationResponseDto result = service.sendNotification(requestDto);

        // then
        // отправка
        verify(sender).send(entityAfterFirstSave);

        // два сохранения
        verify(repository, times(2)).save(notificationCaptor.capture());
        List<Notification> savedNotifications = notificationCaptor.getAllValues();

        Notification firstSave = savedNotifications.get(0);
        Notification secondSave = savedNotifications.get(1);

        assertEquals(NotificationStatus.PENDING, firstSave.getStatus());
        assertNotNull(firstSave.getCreatedAt());

        assertEquals(NotificationStatus.SENT, secondSave.getStatus());
        assertNotNull(secondSave.getSentAt());

        // mapper.toDto вызван с последним состоянием
        verify(mapper).toDto(entityAfterSecondSave);

        assertEquals(entityAfterSecondSave.getId(), result.getId());
        assertEquals(NotificationStatus.SENT, result.getStatus());
    }

    @Test
    void sendNotification_shouldSaveFailed_whenSenderThrowsException() {
        // given
        NotificationRequestDto requestDto = new NotificationRequestDto();

        Notification entityBeforeSave = new Notification();

        Notification entityAfterFirstSave = new Notification();
        entityAfterFirstSave.setId(UUID.randomUUID());
        entityAfterFirstSave.setStatus(NotificationStatus.PENDING);

        Notification entityAfterSecondSave = new Notification();
        entityAfterSecondSave.setId(entityAfterFirstSave.getId());
        entityAfterSecondSave.setStatus(NotificationStatus.FAILED);
        entityAfterSecondSave.setErrorMessage("SMTP error");

        NotificationResponseDto responseDto = new NotificationResponseDto();
        responseDto.setId(entityAfterSecondSave.getId());
        responseDto.setStatus(NotificationStatus.FAILED);
        responseDto.setErrorMessage("SMTP error");

        when(mapper.toEntity(requestDto)).thenReturn(entityBeforeSave);
        when(repository.save(any(Notification.class)))
                .thenReturn(entityAfterFirstSave)   // после установки PENDING
                .thenReturn(entityAfterSecondSave); // после FAILED
        doThrow(new RuntimeException("SMTP error"))
                .when(sender).send(entityAfterFirstSave);
        when(mapper.toDto(entityAfterSecondSave)).thenReturn(responseDto);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // when
        NotificationResponseDto result = service.sendNotification(requestDto);

        // then
        verify(sender).send(entityAfterFirstSave);
        verify(repository, times(2)).save(notificationCaptor.capture());

        List<Notification> saved = notificationCaptor.getAllValues();
        Notification firstSave = saved.get(0);
        Notification secondSave = saved.get(1);

        assertEquals(NotificationStatus.PENDING, firstSave.getStatus());
        assertEquals(NotificationStatus.FAILED, secondSave.getStatus());
        assertEquals("SMTP error", secondSave.getErrorMessage());

        assertEquals(NotificationStatus.FAILED, result.getStatus());
        assertEquals("SMTP error", result.getErrorMessage());
    }

    @Test
    void getNotification_shouldReturnDto_whenFound() {
        // given
        UUID id = UUID.randomUUID();

        Notification entity = new Notification();
        entity.setId(id);
        entity.setStatus(NotificationStatus.SENT);

        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(id);
        dto.setStatus(NotificationStatus.SENT);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // when
        NotificationResponseDto result = service.getNotification(id);

        // then
        verify(repository).findById(id);
        verify(mapper).toDto(entity);

        assertEquals(id, result.getId());
        assertEquals(NotificationStatus.SENT, result.getStatus());
    }

    @Test
    void getNotification_shouldThrowNotFound_whenNotExists() {
        // given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when / then
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.getNotification(id)
        );

        assertTrue(ex.getMessage().contains("Notification with id " + id + " not found"));
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getNotificationsByCustomerId_shouldReturnEmptyList_forNow() {
        // сейчас метод возвращает List.of()
        List<NotificationResponseDto> result = service.getNotificationsByCustomerId(123L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(repository, mapper, sender);
    }
}