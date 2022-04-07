package com.artezio.artping.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Сущность "Точка" для хранения геоданных
 */
@Getter
@Setter
@Entity
@Table(name = "point", schema = "art_ping")
public class Point  extends AbstractEntity {

    /**
     * Широта
     */
    @Column
    private String latitude;

    /**
     * Долгота
     */
    @Column
    private String longitude;

    /**
     * Точность координат
     */
    @Column
    private String accuracy;

    /**
     * Признак, что долгота и широта определены браузером без ошибок
     */
    @Column
    private boolean isValid;

}