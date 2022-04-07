package com.artezio.artping.service.integration.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.Collection;

/**
 * ДТО для приема календаря по интеграции
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDto extends BaseDto {
    private static final long serialVersionUID = 3163184278871617388L;

    /**
     * Название календаря
     */
    private String name;

    /**
     * День начала недели
     */
    private Integer startWeekDay;

    /**
     * Маска выходных дней
     */
    private int[] weekendMask;

    /**
     * Начало рабочего времени
     */
    private LocalTime workHoursFrom;

    /**
     * Окончание рабочего времени
     */
    private LocalTime workHoursTo;

    /**
     * Список событий - переносы/больничные/отпуска
     */
    private Collection<EventDto> events;

    public CalendarDto addEvent(EventDto eventDto) {
        if (eventDto!=null) {
            eventDto.setCalendar(this);
            getEvents().add(eventDto);
        }
        return this;
    }
}

