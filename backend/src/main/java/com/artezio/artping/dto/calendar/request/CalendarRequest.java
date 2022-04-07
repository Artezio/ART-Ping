package com.artezio.artping.dto.calendar.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Collections;
import java.util.List;

/**
 * ДТО создания календаря
 */
@Data
public class CalendarRequest {

    /**
     * Имя календаря
     */
    @ApiModelProperty(required = true)
    @NotBlank(message = "Name is mandatory")
    private String name;

    /**
     * Начало рабочего дня
     */
    @ApiModelProperty(required = true)
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String workHoursFrom;

    /**
     * Конец рабочего дня
     */
    @ApiModelProperty(required = true)
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String workHoursTo;

    /**
     * Начало недели
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
}
