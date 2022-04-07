package com.artezio.artping.dto.employee.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.*;

/**
 * ДТО для пользователя
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeAndUserDto extends EmployeeDto {
    private static final long serialVersionUID = 1100989835497499800L;

    /**
     * Идентификаторы проектов сотрудника
     */
    private List<UUID> projects = new ArrayList<>();

    /**
     * Идентификаторы проектов, которыми сотрудник руководит
     */
    private List<UUID> managedProjects = new ArrayList<>();

    /**
     * Идентификаторы офисов, которыми сотрудник руководит
     */
    private List<UUID> managedOffices = new ArrayList<>();

    /**
     * Идентификаторы офисов, на которых сотрудник является HR'ом
     */
    private List<UUID> offices = new ArrayList<>();

    /**
     * Признак активности
     */
    private boolean active;
}
