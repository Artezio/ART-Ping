package com.artezio.artping.entity.user;

import com.artezio.artping.entity.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность "Пользователь"
 * <p>
 * Используется для авторизации
 */
@Entity
@Table(name = "user", schema = "art_ping")
@Getter
@Setter
public class UserEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    public static final String IS_ACTIVE = "active";

    /**
     * Логин пользователя
     */
    @Column(nullable = false, unique = true)
    private String login;

    /**
     * Хэш пароля пользователя
     * <p>
     * Используется bcrypt хеш
     */
    @Column(nullable = false)
    private String password;

    /**
     * Признак активности
     */
    @Column
    private boolean active;

    /**
     * Сотрудник
     */
    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

}
