package com.artezio.artping.dto.employee.response;

import com.artezio.artping.dto.calendar.response.EventResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * ДТО с подробной информацией о сотруднике
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
public class EmployeeDetailedResponse extends EmployeeDto {
    private static final long serialVersionUID = 3513178169758451884L;

    /**
     * Является ли календарь персональным
     */
    private Boolean isCustomCalendar;

    /**
     * Имя календаря
     */
    private String calendarName;

    /**
     * Время начала работы
     */
    private String workHoursFrom;

    /**
     * Время окончания работы
     */
    private String workHoursTo;

    /**
     * Числовое представление дня начала недели
     */
    private Integer startWeekDay;

    /**
     * Строковая маска с выходными днями календаря
     * <p>
     * Например: воскресенье, суббота == "0;6"
     */
    private Integer[] weekendDays;

    /**
     * Список событий, привязанных к календарю
     * <p>
     * События: праздничный день, перенос рабочего, больничный и т.д.
     */
    private List<EventResponse> events = new ArrayList<>();

    /**
     * Идентификаторы проектов сотрудника
     */
    private List<UUID> projects = new ArrayList<>();

    /**
     * Идентификаторы проектов, которыми сотрудник руководит
     */
    private List<UUID> managedProjects = new ArrayList<>();

    /**
     * Идентификаторы офисов, которыми сотрудник руководит
     */
    private List<UUID> managedOffices = new ArrayList<>();

    /**
     * Идентификаторы офисов, на которых сотрудник является HR'ом
     */
    private List<UUID> offices = new ArrayList<>();

    /**
     * Идентификатор персонального календаря
     */
    private String customCalendarId;

    /**
     * Идентификатор календаря офиса
     */
    private String officeCalendarId;
}
