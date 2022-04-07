package com.artezio.artping.service.integration.dto;

import com.artezio.artping.entity.calendar.DayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * ДТО для приема события по интеграции
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto extends BaseDto {
    private static final long serialVersionUID = 6880012848273736003L;

    /**
     * Связанный календарь
     */
    private CalendarDto calendar;

    /**
     * Дата события
     */
    private LocalDate date;

    /**
     * Тип события
     */
    private DayType type;


}
