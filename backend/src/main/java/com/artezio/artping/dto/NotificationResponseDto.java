package com.artezio.artping.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ДТО для информации о текущем статусе уведомления сотруднику
 */
@Getter
@Setter
public class NotificationResponseDto {

    /**
     * Признак успешности доставки
     */
    private boolean success;

    /**
     * Идентификатор успшно доставленного уведомления
     */
    private String responseMessageLink;

    /**
     * Ошибки при отправке уведомления
     */
    private Exception exception;

    public NotificationResponseDto() {
    }

    public NotificationResponseDto(boolean success, String responseMessageLink, Exception exception) {
        this.success = success;
        this.responseMessageLink = responseMessageLink;
        this.exception = exception;
    }
}
