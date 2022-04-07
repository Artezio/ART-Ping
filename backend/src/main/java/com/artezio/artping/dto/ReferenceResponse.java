package com.artezio.artping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ДТО со справочной информацией о запрошенной сущности
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceResponse {

    /**
     * Идентификатор сущности
     */
    private String id;

    /**
     * Имя сущности
     */
    private String name;

    /**
     * Описание сущности
     */
    private String description;
}
