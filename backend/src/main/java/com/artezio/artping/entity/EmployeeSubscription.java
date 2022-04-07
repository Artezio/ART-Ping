package com.artezio.artping.entity;

import java.time.LocalDateTime;

import com.artezio.artping.entity.user.Employee;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Сущность "Подписка"
 * <p>
 * Сведения об устройствах (браузерах) сотрудника
 */
@Entity
@Table(name = "employee_subscription", schema = "art_ping")
@Getter
@Setter
public class EmployeeSubscription extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Хранится ИД подписки firebase
     */
    @Column
    private String subscribeId;

    /**
     * Тип устройства пользователя, с которого получен ответ на уведомление
     */
    @Enumerated(EnumType.STRING)
    @Column
    private DeviceTypeEnum type = DeviceTypeEnum.UNDEFINED;

    /**
     * Описание устройства, с которого оформлена подписка
     */
    @Column
    private String userAgent;

    /**
     * Признак валидности подписки
     */
    @Column(name = "is_valid")
    private boolean valid;

    /**
     * Дата/время создания подписки
     */
    @CreationTimestamp
    @Column
    private LocalDateTime creationTime;

    /**
     * Дата/время обновления подписки
     */
    @UpdateTimestamp
    @Column
    private LocalDateTime lastUpdateTime;

    /**
     * Связанный с подпиской сотрудник
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
