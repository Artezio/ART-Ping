package com.artezio.artping.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ДТО для запроса информации о проверках сотрудника на определенный период
 */
@Getter
@Setter
public class EmployeeTestForPeriodRequest {

    /**
     * Идентификатор сотрудника
     */
    @ApiModelProperty(required = true)
    private UUID id;

    /**
     * Начало периода
     */
    @ApiModelProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * Конец периода
     */
    @ApiModelProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
