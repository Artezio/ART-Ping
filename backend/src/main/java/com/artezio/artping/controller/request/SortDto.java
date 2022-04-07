package com.artezio.artping.controller.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * ДТО для сортировки записей по полям
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SortDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Название поля для сортировки
     */
    private String field;

    /**
     * Флаг сортировки по возрастанию
     */
    private boolean asc;

    public static SortDto of(String fieldName) {
        return new SortDto(fieldName, true);
    }

    public static SortDto of(String fieldName, boolean asc) {
        return new SortDto(fieldName, asc);
    }

    public Sort convert() {
        if (asc) {
            return Sort.by(new Sort.Order(Sort.Direction.ASC, field).ignoreCase());
        } else {
            return Sort.by(new Sort.Order(Sort.Direction.DESC, field).ignoreCase());
        }
    }

}
