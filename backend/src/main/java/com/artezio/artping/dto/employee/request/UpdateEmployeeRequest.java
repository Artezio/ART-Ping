package com.artezio.artping.dto.employee.request;

import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.calendar.request.UpdateEventRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * ДТО для обновления сотрудника
 */
@Getter
@Setter
public class UpdateEmployeeRequest {

    /**
     * Электронная почта
     */
    private String email;

    /**
     * Имя
     */
    private String firstName;

    /**
     * Фамилия
     */
    private String lastName;

    /**
     * Логин
     */
    private String login;

    /**
     * Офис сотрудника
     */
    private String baseOffice;

    /**
     * Признак того, что календарь является персональным
     */
    private Boolean isCustomCalendar = false;

    // Calendar Request
    /**
     * Имя календаря сотрудника
     */
    @NotBlank(message = "Name is mandatory")
    private String calendarName;

    /**
     * Время начала работы
     */
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String workHoursFrom;

    /**
     * Время окончания работы
     */
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String workHoursTo;

    /**
     * Числовое представление дня начала недели
     */
    @Min(0)
    @Max(6)
    private Integer startWeekDay;

    /**
     * Строковая маска с выходными днями календаря
     * <p>
     * Например: воскресенье, суббота == "0;6"
     */
    @Size(max = 7)
    @NotNull
    private Integer[] weekendDays;

    /**
     * Список событий, привязанных к календарю
     * <p>
     * События: праздничный день, перенос рабочего, больничный и т.д.
     */
    private List<UpdateEventRequest> events = Collections.emptyList();

    /**
     * Идентификаторы проектов сотрудника
     */
    private List<UUID> projects = Collections.emptyList();

    /**
     * Идентификаторы проектов, которыми сотрудник руководит
     */
    private List<UUID> managedProjects = Collections.emptyList();

    /**
     * Роли сотрудника
     */
    private List<RoleEnum> roles = Collections.emptyList();

    /**
     * Идентификаторы офисов, которыми сотрудник руководит
     */
    private List<UUID> managedOffices = Collections.emptyList();

    /**
     * Идентификаторы офисов, на которых сотрудник является HR'ом
     */
    private List<UUID> offices = Collections.emptyList();
}
