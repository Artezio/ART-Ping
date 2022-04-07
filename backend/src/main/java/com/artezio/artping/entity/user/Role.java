package com.artezio.artping.entity.user;

import com.artezio.artping.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Справочник ролей
 */
@Entity
@Table(name = "role", schema = "art_ping")
@Getter
@Setter
public class Role extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Код роли
     */
    @Column
    private String code;

    /**
     * Название роли
     */
    @Column
    private String name;

}
