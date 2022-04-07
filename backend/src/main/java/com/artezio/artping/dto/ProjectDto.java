package com.artezio.artping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * ДТО информации о проекте
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    /**
     * Идентификатор проекта
     */
    private String id;

    /**
     * Название проекта
     */
    private String name;

    /**
     * Идентификаторы руководителей проекта
     */
    private List<String> pmIds = new ArrayList<>();

    /**
     * Идентификаторы сотрудников проекта
     */
    private List<String> employeeIds = new ArrayList<>();
}
