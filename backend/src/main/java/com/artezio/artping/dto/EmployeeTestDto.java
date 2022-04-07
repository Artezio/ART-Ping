package com.artezio.artping.dto;

import com.artezio.artping.dto.json.ZonedDateTimeSerializer;
import com.artezio.artping.entity.NotificationStatusEnum;
import com.artezio.artping.entity.TestStatusEnum;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * ДТО с информацией о проверке сотрудника
 */
@Getter
@Setter
public class EmployeeTestDto {

  /**
   * Идентификатор проверки
   */
  @ApiModelProperty(required = true)
  private String id;

  /**
   * Статус проверки
   */
  @ApiModelProperty(required = true)
  private TestStatusEnum status;

  /**
   * Статус уведомления
   */
  private NotificationStatusEnum notification;

  /**
   * Время начала проверки
   */
  @ApiModelProperty(required = true)
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime startTime;

  /**
   * Время ответа на проверку
   */
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime responseTime;

  /**
   * Информация о клиенте
   */
  private String userAgent;

  /**
   * Геоданные клиента на момент проверки
   */
  private PointDto point;

}
