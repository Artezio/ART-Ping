package com.artezio.artping.dto;

import com.artezio.artping.dto.json.UtcLocalDateTimeSerializer;
import com.artezio.artping.entity.TestStatusEnum;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ДТО с информацией о последней проверке сотрудника
 */
@Data
public class EmployeeLastTestInfo {

    /**
     * Идентификатор проверки
     */
    private String id;

    /**
     * Начало проверки
     */
    @JsonSerialize(using = UtcLocalDateTimeSerializer.class)
    private LocalDateTime startTime;

    /**
     * Статус проверки
     */
    private TestStatusEnum status;
}
