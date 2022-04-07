package com.artezio.artping.dto.user.request;

import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.calendar.request.EventRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * ДТО с информацией о создаваемом пользователе и сущности сотрудника
 */
@Data
public class UserCreationRequest {

    /**
     * Фамилия
     */
    @ApiModelProperty(required = true)
    private String lastName;

    /**
     * Имя
     */
    @ApiModelProperty(required = true)
    private String firstName;

    /**
     * Логин
     */
    @ApiModelProperty(required = true)
    private String login;

    /**
     * Пароль
     */
    @ApiModelProperty(required = true)
    private String password;

    /**
     * Электронная почта
     */
    @ApiModelProperty(required = true)
    private String email;

    /**
     * Офис сотрудника
     */
    @ApiModelProperty(required = true)
    private String baseOffice;

    /**
     * Признак персонального календаря
     */
    private Boolean isCustomCalendar = false;

    // Calendar Request
    /**
     * Имя календаря
     */
    @NotBlank(message = "Name is mandatory")
    private String calendarName;

    /**
     * Начало рабочего дня
     */
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String workHoursFrom;

    /**
     * Конец рабочего дня
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
     * Маска выходных дней
     */
    @Size(max = 7)
    @NotNull
    private Integer[] weekendDays;

    /**
     * Список событий календаря
     */
    private List<EventRequest> events = Collections.emptyList();

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
