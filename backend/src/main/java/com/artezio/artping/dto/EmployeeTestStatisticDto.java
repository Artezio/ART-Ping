package com.artezio.artping.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * ДТО для статистики пройденных проверок сотрудником
 */
@Data
@Getter @Setter
public class EmployeeTestStatisticDto {

    /**
     * Имя сотрудника
     */
    private String firstName;

    /**
     * Фамилия сотрудника
     */
    private String lastName;

    /**
     * Статистика о пройденных проверках по дням (всего/пройденные)
     */
    Map<LocalDate, String> dayResult = new HashMap<>();

    /**
     * Процентное соотношение пройденных проверок на запрошенный период
     */
    private double percentage;
}
