package com.artezio.artping.dto;

import com.artezio.artping.validator.TestSuccessfulMinutesRestriction;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.artezio.artping.validator.SystemSettingsRestrictions.*;

/**
 * ДТО с информацией о системных настройках
 */
@Data
@TestSuccessfulMinutesRestriction
public class SystemSettingsDto {

    /**
     * Допустимое время ответа на проверку
     */
    @Min(value = MIN_TEST_SUCCESSFUL_MINUTES,
            message = "Допустимое время ответа на проверку меньше минимально допустимого значения")
    @Max(value = MAX_TEST_SUCCESSFUL_MINUTES,
            message = "Допустимое время ответа на проверку больше максимально допустимого значения")
    @ApiModelProperty(notes = "Допустимое время ответа на проверку в минутах",
            name = "testSuccessfulMinutes", required = true)
    private Integer testSuccessfulMinutes;

    /**
     * Время ожидания ответа на проверку
     */
    @Min(value = MIN_TEST_NO_RESPONSE_MINUTES,
            message = "Время ожидания ответа на проверку меньше минимально допустимого значения")
    @Max(value = MAX_TEST_NO_RESPONSE_MINUTES,
            message = "Время ожидания ответа на проверку больше максимально допустимого значения")
    @ApiModelProperty(notes = "Время ожидания ответа на проверку в минутах",
            name = "testNoResponseMinutes", required = true)
    private Integer testNoResponseMinutes;

    /**
     * Рекомендованное количество проверок в день
     */
    @Min(value = MIN_RECOMMENDED_DAILY_CHECKS,
            message = "Заданное количество проверок меньше минимально допустимого значения")
    @Max(value = MAX_RECOMMENDED_DAILY_CHECKS,
            message = "Заданное количество проверок больше максимально допустимого значения")
    @ApiModelProperty(notes = "Рекомендованное количество проверок в день",
            name = "recommendedDailyChecks", required = true)
    private Integer recommendedDailyChecks;

    /**
     * Время жизни токена авторизации
     */
    @ApiModelProperty(notes = "Время жизни токена авторизации", name = "jwtTokenValidity", required = true)
    private Long jwtTokenValidity;
}
