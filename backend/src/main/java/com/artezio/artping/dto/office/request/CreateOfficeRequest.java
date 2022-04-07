package com.artezio.artping.dto.office.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

/**
 * ДТО с информацией о создаваемом офисе
 */
@Data
public class CreateOfficeRequest {

  /**
   * Название офиса
   */
  @ApiModelProperty(required = true)
  @NotBlank
  private String name;

  /**
   * Идентификатор календаря
   */
  @ApiModelProperty(required = true)
  @NotBlank
  private String calendarId;

  /**
   * Таймзона
   */
  @ApiModelProperty(required = true)
  @NotBlank
  @Pattern(regexp = "UTC[+-]([0-9]|1[0-2])")
  private String timezone;
}
