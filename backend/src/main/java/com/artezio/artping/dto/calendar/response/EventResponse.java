package com.artezio.artping.dto.calendar.response;

import com.artezio.artping.entity.calendar.DayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО с информацией о событии
 */
@Data
@Getter @Setter
public class EventResponse {

    /**
     * Идентификатор события
     */
    @ApiModelProperty(required = true)
    private String id;

    /**
     * Описание события
     */
    private String title;

    /**
     * Тип события
     */
    @ApiModelProperty(required = true)
    private DayType type;

    /**
     * Дата события
     */
    @ApiModelProperty(required = true)
    private String date;
}

