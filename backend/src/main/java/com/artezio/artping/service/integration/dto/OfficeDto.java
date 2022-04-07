package com.artezio.artping.service.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

/**
 * ДТО для приема офиса по интеграции
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDto extends BaseDto {
    private static final long serialVersionUID = -1032725570772285997L;

    /**
     * Название офиса
     */
    private String name;

    /**
     * Связанный календарь
     */
    private CalendarDto calendar;

    /**
     * Список сотрудников офиса
     */
    private Collection<EmployeeDto> employees;

    /**
     * TimeZoneId, который использует офис
     */
    private String timezone;

    public OfficeDto addEmployee(EmployeeDto employeeDto) {
        if (employeeDto!=null) {
            employeeDto.setOffice(this);
            getEmployees().add(employeeDto);
        }
        return this;
    }
}
