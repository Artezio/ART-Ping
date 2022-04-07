package com.artezio.artping.entity;

import java.time.ZonedDateTime;

import com.artezio.artping.entity.user.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность "Проверка"
 */
@Entity
@Table(name = "employee_test", schema = "art_ping")
@Getter
@Setter
public class EmployeeTest extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATUS = "status";
    public static final String START_TIME = "startTime";

    /**
     * Иднетификатор сотрудника
     */
    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private UUID employeeId;

    /**
     * Сорудник
     */
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * Статус проверки
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TestStatusEnum status;

    /**
     * Время начала проверки, смещенное во временную зону сотрудника
     */
    @Column
    private ZonedDateTime startTime;

    /**
     * Время получения ответа во временной зоне сотрудника
     */
    @Column
    private ZonedDateTime responseTime;

    /**
     * Тип устройства, с которого получен ответ
     */
    @Enumerated(EnumType.STRING)
    @Column
    private DeviceTypeEnum deviceType = DeviceTypeEnum.UNDEFINED;

    /**
     * Описание устройства, с которого получен ответ
     */
    @Column
    private String userAgent;

    /**
     * Статус отправленного извещения
     */
    @Enumerated(EnumType.STRING)
    @Column
    private NotificationStatusEnum notification = NotificationStatusEnum.NOT_SENT;

    /**
     * Дата/время обновления проверки
     */
    @UpdateTimestamp
    @Column
    private LocalDateTime lastUpdateTime;

    /**
     * Геоданные сотрудника на момент прохождения проверки
     */
    @OneToOne
    @JoinColumn(name = "point_id", referencedColumnName = "id")
    private Point point;

}
