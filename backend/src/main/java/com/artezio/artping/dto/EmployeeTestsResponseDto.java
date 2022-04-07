package com.artezio.artping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ответ на создание/отмену проверок сотрудников
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTestsResponseDto {

    /**
     * Информация о возможных ошибках при создании/отмене проверок
     */
    private String message;
}
