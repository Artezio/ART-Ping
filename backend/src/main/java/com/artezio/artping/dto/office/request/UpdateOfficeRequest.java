package com.artezio.artping.dto.office.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * ДТО с информацией об изменяемом офисе
 */
@Data
public class UpdateOfficeRequest {

  /**
   * Идентификатор офиса
   */
  @ApiModelProperty(required = true)
  private String id;

  /**
   * Название офиса
   */
  @ApiModelProperty(required = true)
  private String name;

  /**
   * Ссылка на календарь
   */
  @ApiModelProperty(required = true)
  private String calendarId;

  /**
   * Таймзона
   */
  @ApiModelProperty(required = true)
  @NotBlank
  @Pattern(regexp = "UTC[+-]([0-9]|1[0-2])")
  private String timezone;
}
