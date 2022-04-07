package com.artezio.artping.dto.employee.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Дто для сотрудника, базовое, без информации о проверках
 */
@Data
@Getter @Setter
public class EmployeeDto implements Serializable {
    private static final long serialVersionUID = 5612277442459791866L;

    /**
     * Идентификатор сотрудника
     */
    @ApiModelProperty(required = true, notes = "Идентификатор сотрудника", name = "id")
    private String id;

    /**
     * Логин сторудника
     */
    @ApiModelProperty(required = true, notes = "Логин сторудника", name = "login")
    private String login;

    /**
     * Почта сторудника
     */
    @ApiModelProperty(required = true, notes = "Почта сторудника", name = "email")
    private String email;

    /**
     * Имя
     */
    @ApiModelProperty(required = true, notes = "Имя", name = "firstName")
    private String firstName;

    /**
     * Фамилия
     */
    @ApiModelProperty(required = true, notes = "Фамилия", name = "lastName")
    private String lastName;

    /**
     * Идентификатор офиса, к которому привязан сотрудник
     */
    @ApiModelProperty(notes = "Идентификатор офиса, к которому привязан сотрудник", name = "baseOffice")
    private String baseOffice;

    /**
     * Идентификатор персонального календаря
     */
    @ApiModelProperty(notes = "Идентификатор персонального календаря", name = "calendarId")
    private String calendarId;

    /**
     * Список имен ролей
     */
    @ApiModelProperty(notes = "Список имен ролей", name = "roles")
    private Collection<String> roles;

    @JsonIgnore
    private boolean deleted;

    /**
     * Дата увольнения сотрудника
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "Дата увольнения сотрудника", name = "leaveDate")
    private LocalDate leaveDate;

    @JsonIgnore
    private LocalDateTime updated;

}
