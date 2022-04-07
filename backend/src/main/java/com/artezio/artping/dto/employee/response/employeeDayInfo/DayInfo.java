package com.artezio.artping.dto.employee.response.employeeDayInfo;

import com.artezio.artping.dto.json.LocalDateSerializer;
import com.artezio.artping.entity.calendar.DayType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * ДТО для информации о дне сотрудника (рабочий/нерабочий, информация о тестах в этот день)
 */
@Getter
@Setter
public class DayInfo {

    /**
     * Дата
     */
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    /**
     * Тип дня
     */
    private DayType type;

    /**
     * Тесты сотрудника в этот день
     */
    private List<DayTestDto> tests;
}
