package com.artezio.artping.entity.office;

import com.artezio.artping.entity.AbstractEntity;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.user.Employee;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность "Офис"
 */
@Entity
@Table(name = "office", schema = "art_ping")
@Getter
@Setter
public class Office extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Название офиса
     */
    @Column
    private String name;

    /**
     * Идентификатор календаря, который использует офис
     */
    @Column(name = "calendar_id")
    private UUID calendarId;

    /**
     * Календарь, который использует офис
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", insertable = false, updatable = false)
    private Calendar calendar;

    /**
     * TimeZoneId, который использует офис
     */
    @Column(name = "timezone")
    private String timezone;

    /**
     * Сотрудники, руководящие данным офисом
     */
    @ManyToMany(mappedBy = "managedOffices")
    private List<Employee> managingEmployees = new ArrayList<>();

    /**
     * Все сотрудники офиса
     */
    @OneToMany(mappedBy = "baseOffice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> officeEmployees = new ArrayList<>();

}
