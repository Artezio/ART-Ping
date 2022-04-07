package com.artezio.artping.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО с информацией об уведомлениях для сотрудника
 */
@Getter
@Setter
public class NotificationsResponseDto {

    /**
     * Список отправленных уведомлений
     */
    private List<NotificationResponseDto> notificationResponseDtoList;

    /**
     * Статус отправки
     */
    private boolean success;

    /**
     * Информация о возможной ошибке
     */
    private Exception exception;
}
