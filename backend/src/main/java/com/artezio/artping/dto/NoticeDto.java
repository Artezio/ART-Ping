package com.artezio.artping.dto;

import com.artezio.artping.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Информация уведомления сотруднику
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDto {

    /**
     * Идентификатор уведомления
     */
    private UUID id;

    /**
     * Содержимое уведомления
     */
    private String text;

    /**
     * Просмотрено ли уведомление
     */
    private boolean viewed;

    /**
     * Тип уведомления
     */
    private NotificationType type;

}
