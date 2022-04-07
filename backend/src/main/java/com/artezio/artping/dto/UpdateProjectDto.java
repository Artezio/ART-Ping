package com.artezio.artping.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ДТО с информацией об обновляемом проекте
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectDto {

  /**
   * Идентификатор проекта
   */
  private String id;

  /**
   * Название проекта
   */
  @ApiModelProperty(required = true)
  private String name;

  /**
   * Активность проекта
   */
  @ApiModelProperty(required = true)
  private Boolean active = true;

  /**
   * Идентификаторы руководителей проекта
   */
  private List<UUID> pmIds = new ArrayList<>();

  /**
   * Идентификаторы сотрудников проекта
   */
  private List<UUID> employeeIds = new ArrayList<>();

}
