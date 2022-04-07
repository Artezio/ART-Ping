package com.artezio.artping.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО с информацией о текущем пользователе
 */
@Getter
@Setter
public class ContextUserDto {

    /**
     * Информация о пользователе
     */
    private String username;

    /**
     * Роли пользователя
     */
    private List<RoleEnum> roles;

}
