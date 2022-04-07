package com.artezio.artping.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Дто запроса для отмены проверок сотруднику на определенный день.
 */
@Data
public class CancelEmployeeAutoChecksRequest {

    /**
     * Идентификатор сотрудника
     */
    @ApiModelProperty(required = true)
    @NotNull
    private UUID id;

    /**
     * Дата на которую мы отменяем проверки
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(required = true)
    private LocalDate date;
}
