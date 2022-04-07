package com.artezio.artping.entity;

import com.artezio.artping.entity.user.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность "Уведомления"
 */
@Getter
@Setter
@Entity
@Table(name = "notification", schema = "art_ping")
public class Notification extends AbstractEntity {

    /**
     * Содержимое уведомления
     */
    @Column
    private String text;

    /**
     * Просмотрено ли уведомление
     */
    @Column
    private boolean viewed;

    /**
     * Тип уведомления
     */
    @Column
    private NotificationType type;

    /**
     * Сотрудник, к которому отправляется уведомление
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * Дата/время создания уведомления
     */
    @CreationTimestamp
    @Column(updatable = false, insertable = false)
    private LocalDateTime creationTime;

}
