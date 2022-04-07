package com.artezio.artping.service.integration.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Базовый ДТО для приема данных по интеграции
 */
@Getter @Setter
public abstract class BaseDto implements Serializable {
    private static final long serialVersionUID = 2748323565746944245L;

    /**
     * Идентификатор записи импорта
     */
    private Long id;
}
