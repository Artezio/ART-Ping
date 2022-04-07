package com.artezio.artping.dto.employee.response;

import com.artezio.artping.dto.EmployeeTestDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Дто для сотрудника с информацией о проверках
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
public class EmployeeWithTestDto extends EmployeeDto {
    private static final long serialVersionUID = -7471955719890352263L;

    /**
     * Последняя запущенная проверка
     */
    @ApiModelProperty(notes = "Последняя запущенная проверка", name = "lastEmployeeTest")
    private EmployeeTestDto lastEmployeeTest;

    /**
     * Список проверок по сотруднику
     */
    @ApiModelProperty(notes = "Список проверок по сотруднику", name = "employeeTestList")
    private List<EmployeeTestDto> employeeTestList;
}
