package com.artezio.artping.service.integration;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Setter @Getter
public class ExportEmployeeTestRequest {

    @ApiModelProperty(notes = "Список экспортируемых сотрудников", name = "employeeIds")
    @NotNull
    private List<UUID> employeeIds = new ArrayList<>();

    @ApiModelProperty(notes = "Начало периода запрашиваемых тестов сотрудника", name = "startPeriod")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate startPeriod;

    @ApiModelProperty(notes = "Конец периода запрашиваемых тестов сотрудника", name = "endPeriod")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate endPeriod;
}
