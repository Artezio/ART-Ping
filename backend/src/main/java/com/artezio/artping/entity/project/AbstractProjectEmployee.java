package com.artezio.artping.entity.project;

import com.artezio.artping.entity.AbstractEntity;
import com.artezio.artping.entity.user.Employee;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorOptions;

import java.util.UUID;

/**
 * Абстрактная сущность для связи сотрудников и проектов
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_manager")
@DiscriminatorOptions(force = true)
@Table(name = "project_employee", schema = "art_ping")
@Getter
@Setter
public abstract class AbstractProjectEmployee extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    public static final String PROJECT_ID = "projectId";
    public static final String EMPLOYEE_ID = "employeeId";

    /**
     * Идентификатор проекта
     */
    @Column(name = "project_id", insertable = false, updatable = false)
    private UUID projectId;

    /**
     * Идентификатор сотрудника
     */
    @Column(name = "employee_id", insertable = false, updatable = false)
    private UUID employeeId;

    /**
     * Сотрудник
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * Проект
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
