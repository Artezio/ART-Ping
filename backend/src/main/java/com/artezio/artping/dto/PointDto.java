package com.artezio.artping.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * ДТО геоданных
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {

    /**
     * Широта
     */
    @ApiModelProperty(required = true)
    @NotBlank(message = "Latitude mustn't be empty")
    private String latitude;

    /**
     * Долгота
     */
    @ApiModelProperty(required = true)
    @NotBlank(message = "Longitude mustn't be empty")
    private String longitude;

    /**
     * Точность координат
     */
    @ApiModelProperty
    private String accuracy;

    /**
     * Признак, что долгота и широта определены браузером без ошибок
     */
    private boolean isValid = false;
}
