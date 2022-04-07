package com.artezio.artping.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * ДТО для задания периода, на который будут запрашиваться проверки сотрудников
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Setter @Getter
public class EmployeesTestsPageFilter extends EmployeesPageFilter {
    private static final long serialVersionUID = -7933807901753594013L;

    /**
     * Период отображения данных таблицы, с
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "Период отображения данных таблицы, с", name = "startPeriod")
    private LocalDate startPeriod;

    /**
     * Период отображения данных таблицы, по
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "Период отображения данных таблицы, по", name = "endPeriod")
    private LocalDate endPeriod;

}
