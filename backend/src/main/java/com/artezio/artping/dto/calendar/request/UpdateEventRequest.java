package com.artezio.artping.dto.calendar.request;

import com.artezio.artping.entity.calendar.DayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * ДТО обновления события календаря
 */
@Data
@Getter @Setter
public class UpdateEventRequest {

    /**
     * Идентификатор события
     */
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
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private LocalDate date;
}
