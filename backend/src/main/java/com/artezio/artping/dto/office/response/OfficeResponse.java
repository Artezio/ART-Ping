package com.artezio.artping.dto.office.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ДТО с информацией об офисе после операций над ним
 */
@Data
public class OfficeResponse {

  /**
   * Идентификатор офиса
   */
  @ApiModelProperty
  private String id;

  /**
   * Название
   */
  @ApiModelProperty
  private String name;

  /**
   * Идентификатор календаря
   */
  @ApiModelProperty
  private String calendarId;

  /**
   * Таймзона
   */
  @ApiModelProperty
  private String timezone;
}
