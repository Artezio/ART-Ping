package com.artezio.artping.entity.calendar;

import com.artezio.artping.entity.AbstractEntity;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.user.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Сущность "Календарь"
 */
@Getter
@Setter
@Entity
@Table(name = "calendar", schema = "art_ping")
public class Calendar extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Название календаря
     */
    @Column(name = "name")
    private String name;

    /**
     * Дата/время создания
     */
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    /**
     * Признак активности
     */
    @Column(name = "active")
    private Boolean active;

    /**
     * Числовое представление дня начала недели
     */
    @Column(name = "start_week_day")
    private Integer startWeekDay;

    /**
     * Строковая маска с выходными днями календаря
     * <p>
     * Например: воскресенье, суббота == "0;6"
     */
    @Column(name = "weekend_days")
    private String weekendDays;

    /**
     * Время начала работы
     */
    @Column(name = "work_hours_from")
    private LocalTime workHoursFrom;

    /**
     * Время окончания работы
     */
    @Column(name = "work_hours_to")
    private LocalTime workHoursTo;

    /**
     * Признак того, что календарь привязан к сотруднику
     */
    @Column(name = "custom_calendar")
    private boolean customCalendar;

    /**
     * Список событий, привязанных к календарю
     * <p>
     * События: праздничный день, перенос рабочего, больничный и т.д.
     */
    @OneToMany(mappedBy = "calendar", targetEntity = Event.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Event> events;

    /**
     * Список офисов, которые используют данный календарь
     * <p>
     * Если календарь отмечен как customCalendar, то это календарь конкретного сотрудника,
     * он не должен быть привязван ни к какому офису
     */
    @OneToMany(mappedBy = "calendar", targetEntity = Office.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Office> offices;

    /**
     * Список сотрудников, использующих данный календарь
     */
    @OneToMany(mappedBy = "calendar", targetEntity = Employee.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Employee> employees;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Calendar)) {
            return false;
        }
        Calendar calendar = (Calendar) o;
        return Objects.equals(name, calendar.getName()) &&
                Objects.equals(active, calendar.getActive()) &&
                Objects.equals(startWeekDay, calendar.getStartWeekDay()) &&
                Objects.equals(weekendDays, calendar.getWeekendDays()) &&
                Objects.equals(workHoursFrom, calendar.getWorkHoursFrom()) &&
                Objects.equals(workHoursTo, calendar.getWorkHoursTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, active, startWeekDay, weekendDays, workHoursFrom, workHoursTo);
    }

}

