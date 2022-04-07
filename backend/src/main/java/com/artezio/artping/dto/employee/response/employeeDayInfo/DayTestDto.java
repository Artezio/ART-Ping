package com.artezio.artping.dto.employee.response.employeeDayInfo;

import com.artezio.artping.dto.PointDto;
import com.artezio.artping.dto.json.ZonedDateTimeToLocalTimeSerializer;
import com.artezio.artping.entity.TestStatusEnum;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * ДТО для информации о проверках сотрудника
 */
@Getter
@Setter
public class DayTestDto {

    /**
     * Время начала проверки
     */
    @JsonSerialize(using = ZonedDateTimeToLocalTimeSerializer.class)
    private ZonedDateTime startTime;

    /**
     * Статус проверки
     */
    private TestStatusEnum status;

    /**
     * Время ответа на проверку
     */
    @JsonSerialize(using = ZonedDateTimeToLocalTimeSerializer.class)
    private ZonedDateTime responseTime;

    /**
     * Геоданные клиента на момент проверки
     */
    private PointDto point;
}
