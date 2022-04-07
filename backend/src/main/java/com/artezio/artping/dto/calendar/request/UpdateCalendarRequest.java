package com.artezio.artping.dto.calendar.request;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

/**
 * ДТО для обновления календаря
 */
@Data
public class UpdateCalendarRequest {

  /**
   * Имя календаря
   */
  private String name;

  /**
   * Начало рабочего дня
   */
  @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
  private String workHoursFrom;

  /**
   * Конец рабочго дня
   */
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
  @Size(max = 6)
  @NotNull
  private Integer[] weekendDays;

  /**
   * Список событий календаря
   */
  private List<UpdateEventRequest> events;

}
