package com.artezio.artping.entity.user;

import com.artezio.artping.entity.*;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.project.AbstractProjectEmployee;
import com.artezio.artping.entity.project.ProjectEmployee;
import com.artezio.artping.entity.project.ProjectPM;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность "Сотрудник"
 */
@Entity
@Table(name = "employee", schema = "art_ping")
@Getter
@Setter
public class Employee extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    public static final String LOGIN = "login";
    public static final String LAST_NAME = "lastName";
    public static final String FIRST_NAME = "firstName";
    public static final String OFFICE_ID = "officeId";
    public static final String BASE_OFFICE = "baseOffice";
    public static final String DELETED = "deleted";
    public static final String LEAVE_DATE = "leaveDate";
    public static final String PROJECT_EMPLOYEES = "projectEmployees";
    public static final String EMPLOYEE_TESTS = "employeeTestList";
    public static final String ROLES = "roles";
    public static final String USER = "user";

    /**
     * Связанная с сотрудником запись о пользовтаеле
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    /**
     * Логин
     */
    @Column(nullable = false)
    private String login;

    /**
     * Электронная почта
     */
    @Column
    private String email;

    /**
     * Имя
     */
    @Column
    private String firstName;

    /**
     * Фамилия
     */
    @Column
    private String lastName;

    /**
     * Дата увольнения
     */
    @Column
    private LocalDate leaveDate;

    /**
     * Временная метка последних обновлений записи
     */
    @UpdateTimestamp
    @Column
    private LocalDateTime updated;

    /**
     * Офис сотрудника
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office baseOffice;

    /**
     * Список офисов, в которых сотрудник является директором
     */
    @ManyToMany
    @JoinTable(name = "employee_director", schema = "art_ping",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "office_id"))
    private List<Office> managedOffices = new ArrayList<>();

    /**
     * Календарь сотрудника
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    /**
     * Список подписок устройств сотрудника
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "employee_id")
    private List<EmployeeSubscription> subscriptions = new ArrayList<>();

    /**
     * Список связей с проектами, с которыми связан сотрудник
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectEmployee> projectEmployees = new ArrayList<>();

    /**
     * Список связей с проектами, с которыми связан сотрудник в качестве ПМа
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPM> projectPMs = new ArrayList<>();

    /**
     * Список связей со всеми проектами, с которыми связан сотрудник
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AbstractProjectEmployee> abstractProjectEmployees = new ArrayList<>();

    /**
     * Набор ролей, выданных сотруднику
     */
    @ManyToMany
    @JoinTable(name = "employee_role", schema = "art_ping",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Оффисы, в которых сотрудник является HR-ом
     */
    @ManyToMany
    @JoinTable(name = "employee_office", schema = "art_ping",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "office_id"))
    private List<Office> offices = new ArrayList<>();

    /**
     * Последняя запущенная проверка
     */
    @Transient
    private EmployeeTest lastEmployeeTest;

    /**
     * Список проверок по сотруднику
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "employee_id")
    private List<EmployeeTest> employeeTestList;

    /**
     * Название проекта сотрдуника для сортировки по возрастанию
     */
    @Column
    private String ascProjectName;

    /**
     * Название проекта сотрдуника для сортировки по убыванию
     */
    @Column
    private String descProjectName;

    /**
     * Наименование роли сотрудника для сортировки по возрастанию
     */
    @Column
    private String ascRoleName;

    /**
     * Наименование роли сотрудника для сортировки по убыванию
     */
    @Column
    private String descRoleName;
}
