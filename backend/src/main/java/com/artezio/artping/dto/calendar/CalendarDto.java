package com.artezio.artping.dto.calendar;

import lombok.*;

/**
 * ДТО с информацией обновленного календаря
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarDto {

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
   * Начало недели
   */
  private Integer startWeekDay;

  /**
   * Начало рабочего дня
   */
  private String workHoursFrom;

  /**
   * Конец рабочего дня
   */
  private String workHoursTo;
}
