package com.artezio.artping.dto.calendar.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ДТО ответа при создании календаря
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedCalendarResponse {

  /**
   * Идентификатор календаря
   */
  private String id;

  /**
   * Имя календаря
   */
  private String name;

  /**
   * Признак активный/неактивный
   */
  private Boolean active;

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
}
