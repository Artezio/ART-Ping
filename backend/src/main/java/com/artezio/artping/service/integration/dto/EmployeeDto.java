package com.artezio.artping.service.integration.dto;

import com.artezio.artping.dto.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ДТО для приема сотрудника по интеграции
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto extends BaseDto {
    private static final long serialVersionUID = 1342749934743839997L;

    /**
     * имя сотрудника
     */
    private String firstName;

    /**
     * фамилия сотрудника
     */
    private String lastName;

    /**
     * логин сотрудника
     */
    private String login;

    /**
     * пароль сотрудника
     */
    private String email;

    /**
     * пароль сотрудника
     */
    private String password;

    /**
     * связанный пользовательский календарь
     */
    private CalendarDto calendar;

    /**
     * связный офис
     */
    private OfficeDto office;

    /**
     * роли для сотрудника
     */
    private List<RoleEnum> roles;
}
