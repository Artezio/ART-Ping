package com.artezio.artping.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ДТО с информацией о создаваемом проекте
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectDto {

    /**
     * Название проекта
     */
    @ApiModelProperty(required = true)
    private String name;

    /**
     * Список руководителей проекта
     */
    private List<UUID> pmIds = new ArrayList<>();

    /**
     * Список сотрудников проекта
     */
    private List<UUID> employeeIds = new ArrayList<>();
}
