package com.artezio.artping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ДТО с информацией о роли сотрудника
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo {

    /**
     * Кодовое название роли
     */
    private String code;

    /**
     * Текстовое представление роли
     */
    private String name;
}
