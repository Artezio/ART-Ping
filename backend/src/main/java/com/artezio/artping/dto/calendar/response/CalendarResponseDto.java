package com.artezio.artping.dto.calendar.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * ДТО с информацией о календаре и офисах в которых данный календарь используется
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarResponseDto {

    /**
     * Идентификатор календаря
     */
    private String id;

    /**
     * Признак активный/неактивный
     */
    private boolean active;

    /**
     * Имя календаря
     */
    private String name;

    /**
     * Дата/время создания
     */
    private String creationTime;

    /**
     * Начало недели
     */
    private Integer startWeekDay;

    /**
     * Маска выходных дней
     */
    private Integer[] weekendDays;

    /**
     * Начало рабочего дня
     */
    private String workHoursFrom;

    /**
     * Конец рабочего дня
     */
    private String workHoursTo;

    /**
     * Офисы, в которых календарь используется
     */
    private List<UUID> offices;

    /**
     * Признак персонального календаря
     */
    @JsonIgnore
    private boolean customCalendar;
}
