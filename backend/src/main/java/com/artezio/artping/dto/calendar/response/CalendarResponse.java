package com.artezio.artping.dto.calendar.response;

import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ДТО с информацией о календаре
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarResponse {

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
   * Список событий календаря
   */
  @ApiModelProperty(required = true)
  private Set<EventResponse> events;
}
