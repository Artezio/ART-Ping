package com.artezio.artping.dto;

import static java.util.Objects.nonNull;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Период проверок
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDto {

    /**
     * Начало периода
     */
    private LocalTime startTime;

    /**
     * Конец периода
     */
    private LocalTime endTime;

    /**
     * Проверка правильности периода
     *
     * @return правильный ли период
     */
    public boolean isValid() {
        return nonNull(startTime) && nonNull(endTime) && startTime.isBefore(endTime);
    }


}
