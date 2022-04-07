package com.artezio.artping.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * ДТО для фильтрации сотрудников
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Setter @Getter
public class EmployeesPageFilter extends SearchStringFilter {
    private static final long serialVersionUID = -7933807901753594013L;

    /**
     * Идентификатор офиса
     */
    @ApiModelProperty(notes = "Идентификатор офиса", name = "officeId")
    private UUID officeId;

    /**
     * Идентификатор проекта
     */
    @ApiModelProperty(notes = "Идентификатор проекта", name = "projectId")
    private UUID projectId;

    /**
     * Роли, можно указывать через ; несколько ролей в одну строку
     */
    @ApiModelProperty(notes = "Роль", name = "roleName")
    private String roleName;

    /**
     * Признак отбора только активных (не удалённых) или всех пользователей
     */
    @ApiModelProperty(notes = "Отбор только активных пользователей", name = "onlyActiveEmployees")
    private boolean onlyActiveEmployees = true;
}