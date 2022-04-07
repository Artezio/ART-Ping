package com.artezio.artping.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Дто запроса для отмены проверок сотрудникам на определенный период.
 */
@Data
public class CancelEmployeeListAutoChecksRequest {

    /**
     * Список идентификаторов сотрудников
     */
    @ApiModelProperty(required = true)
    private List<UUID> ids = new ArrayList<>();

    /**
     * Старт периода для отмены проверок
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(required = true)
    private LocalDate startDate;

    /**
     * Конец периода для отмены проверок
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(required = true)
    private LocalDate endDate;
}
