package com.artezio.artping.dto.office;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ДТО с информацией об офисе и список директоров данного офиса
 */
@Getter
@Setter
public class OfficeDto {

  /**
   * Идентификатор офиса
   */
  private String id;

  /**
   * Название
   */
  private String name;

  /**
   * Идентификатор календаря
   */
  private String calendarId;

  /**
   * Таймзона
   */
  private String timezone;

  /**
   * Идентификаторы директоров данного офиса
   */
  private List<String> directorIds;
}
