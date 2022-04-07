package com.artezio.artping.dto.employee.response.employeeDayInfo;

import com.artezio.artping.dto.RoleInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * ДТО для информации о сотруднике с проверками на определенный период
 */
@Getter
@Setter
public class EmployeeWithDayInfo {

    /**
     * Идентификатор
     */
    private UUID id;

    /**
     * Имя
     */
    private String firstName;

    /**
     * Фамилий
     */
    private String lastName;

    /**
     * Признак активности
     */
    private boolean active;

    /**
     * Роли
     */
    private List<RoleInfo> roles;

    /**
     * Даты с информацией о типе дня и тестах
     */
    private List<DayInfo> dates;
}
