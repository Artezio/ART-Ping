package com.artezio.artping.dto.user.response;

import lombok.*;

import java.util.UUID;

/**
 * ДТО с информацией об удаленном пользователе
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDeletedResponse {

    /**
     * Идентификатор пользователя
     */
    private UUID id;

    /**
     * Логин
     */
    private String login;

    /**
     * Признак активности
     */
    private boolean active;
}
