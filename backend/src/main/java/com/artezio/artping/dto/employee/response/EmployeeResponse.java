package com.artezio.artping.dto.employee.response;

import com.artezio.artping.dto.EmployeeTestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
public class EmployeeResponse extends EmployeeDto {
    private static final long serialVersionUID = -691009632481061586L;

    /**
     * Список uuid проектов
     */
    private Collection<String> projects;

    /**
     * Последняя запущенная проверка
     */
    private EmployeeTestDto lastEmployeeTest;
}
