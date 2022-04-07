package com.artezio.artping.entity;

import com.artezio.artping.entity.user.Employee;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущноть "Запрос на восстановление пароля"
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "pwd_recovery", schema = "art_ping")
public class PwdRecovery extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Дата создания запроса на изменение пароля
     */
    @Column(name = "created", nullable = false)
    private LocalDate created;

    /**
     * Признак активности запроса
     * <p>
     * Становится не активным после смены пароля
     */
    //на будущее: можно сделать смену в не активное состояние по прошествии некоторого времени, чтобы ссылка на изменение пароля не была весной
    @Column(name = "active", nullable = false)
    private Boolean active;

    /**
     * Пользователь, которому меняем пароль
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
