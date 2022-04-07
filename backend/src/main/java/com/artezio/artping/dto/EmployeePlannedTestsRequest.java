package com.artezio.artping.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * ДТО для назначения запланированных проверок сотрудникам
 */
@Getter
@Setter
public class EmployeePlannedTestsRequest {

    /**
     * Идентификаторы сотрудников
     */
    @ApiModelProperty(required = true)
    private List<UUID> ids;

    /**
     * Количество проверок
     */
    @Min(message = "Значение проверок не должно быть отрицательным", value = 0)
    private Integer dailyChecks;

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
