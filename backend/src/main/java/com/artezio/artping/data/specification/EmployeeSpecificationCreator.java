package com.artezio.artping.data.specification;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.entity.project.AbstractProjectEmployee;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.project.ProjectEmployee;
import com.artezio.artping.entity.project.ProjectPM;
import com.artezio.artping.entity.user.Employee;

import java.time.LocalDate;
import java.util.*;
import javax.persistence.criteria.*;

import com.artezio.artping.entity.user.Role;
import com.artezio.artping.entity.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class EmployeeSpecificationCreator extends AbstractSpecificationCreator<Employee> {

    /**
     * ИД офиса
     */
    private List<UUID> officeIds;

    /**
     * ИД проекта
     */
    private List<UUID> projectIds;

    /**
     * Фильтр по сотруднику
     * При вводе значений в поле Система выполняет поиск сотрудников по вхождению введенных символов в фамилию
     * или имя сотрудника
     */
    private String name;

    /**
     * Период отображения данных таблицы
     */
    private LocalDate startPeriod;

    /**
     * Идентификаторы сотрудников
     */
    private List<UUID> employeeIds;

    /**
     * Роль
     */
    private Set<Role> roles;

    private boolean onlyActiveEmployees;

    private boolean concatenateEmployeeByOfficesAndProjects = false;

    public void addOfficeId(String officeId) {
        if (!StringUtils.containsWhitespace(officeId)) {
            if (CollectionUtils.isEmpty(officeIds)) {
                officeIds = new ArrayList<>();
            }
            officeIds.add(UUID.fromString(officeId));
        }
    }

    public void addProjectId(UUID projectId) {
        if (nonNull(projectId)) {
            if (CollectionUtils.isEmpty(projectIds)) {
                projectIds = new ArrayList<>();
            }
            projectIds.add(projectId);
        }
    }

    @Override
    public Specification<Employee> create() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> orPredicates = new ArrayList<>();

            if (onlyActiveEmployees) {
                predicates.add(criteriaBuilder.isTrue(root.get(Employee.USER).get(UserEntity.IS_ACTIVE)));
            }

            if (!CollectionUtils.isEmpty(employeeIds)) {
                predicates.add(in(criteriaBuilder, root.get(Employee.ID), employeeIds));
            }

            if (!CollectionUtils.isEmpty(officeIds)) {
                CriteriaBuilder.In<UUID> in = in(criteriaBuilder,
                        root.get(Employee.BASE_OFFICE).get(Office.ID), officeIds);
                if (concatenateEmployeeByOfficesAndProjects) {
                    orPredicates.add(in);
                } else {
                    predicates.add(in);
                }
            }

            if (isSpecFilter()) {
                /*
                 * Особый случай:
                 *  При указании фильтра по проекту и по роли нужно оставлять только тех сотрудников проекта,
                 *  которые обладают указанной ролью относительно к этому проекту
                 */
                if (roles.stream().anyMatch(r -> r.getCode().equals(RoleEnum.PM.getCode()))) {
                    // выбрана роль ПМ
                    Subquery<ProjectPM> subQuery = criteriaQuery.subquery(ProjectPM.class);
                    Root<ProjectPM> projectEmployeeRoot = subQuery.from(ProjectPM.class);
                    subQuery.select(projectEmployeeRoot)
                            .where(criteriaBuilder.and(criteriaBuilder.equal(projectEmployeeRoot.get(AbstractProjectEmployee.EMPLOYEE_ID), root.get(Employee.ID)),
                                    in(criteriaBuilder, projectEmployeeRoot.get(AbstractProjectEmployee.PROJECT_ID), projectIds)));
                    predicates.add(criteriaBuilder.exists(subQuery));
                } else if (roles.stream().anyMatch(r -> r.getCode().equals(RoleEnum.USER.getCode()))) {
                    // выбрана роль Сотрудник
                    Subquery<ProjectEmployee> subQuery = criteriaQuery.subquery(ProjectEmployee.class);
                    Root<ProjectEmployee> projectEmployeeRoot = subQuery.from(ProjectEmployee.class);
                    subQuery.select(projectEmployeeRoot)
                            .where(criteriaBuilder.and(criteriaBuilder.equal(projectEmployeeRoot.get(AbstractProjectEmployee.EMPLOYEE_ID), root.get(Employee.ID)),
                                    in(criteriaBuilder, projectEmployeeRoot.get(AbstractProjectEmployee.PROJECT_ID), projectIds)));
                    predicates.add(criteriaBuilder.exists(subQuery));
                }
            } else {
                if (!CollectionUtils.isEmpty(roles)) {
                    Join<Employee, Role> employeeRoleJoin = root.join(Employee.ROLES, JoinType.INNER);
                    predicates.add(in(criteriaBuilder, employeeRoleJoin, roles));
                }

                if (!CollectionUtils.isEmpty(projectIds)) {
                    Subquery<AbstractProjectEmployee> subQuery = criteriaQuery.subquery(AbstractProjectEmployee.class);
                    Root<AbstractProjectEmployee> projectEmployeeRoot = subQuery.from(AbstractProjectEmployee.class);
                    subQuery.select(projectEmployeeRoot)
                            .where(criteriaBuilder.and(criteriaBuilder.equal(projectEmployeeRoot.get(AbstractProjectEmployee.EMPLOYEE_ID), root.get(Employee.ID)),
                                    in(criteriaBuilder, projectEmployeeRoot.get(AbstractProjectEmployee.PROJECT_ID), projectIds)));
                    Predicate exists = criteriaBuilder.exists(subQuery);
                    if (concatenateEmployeeByOfficesAndProjects) {
                        orPredicates.add(exists);
                    } else {
                        predicates.add(exists);
                    }
                }
            }

            if (!StringUtils.isEmpty(name)) {
                List<Predicate> namePredicates = new ArrayList<>();
                for (String n : name.split(" ", 2)) {
                    String value = prepareForLike(n.toLowerCase());
                    Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(Employee.FIRST_NAME)), value);
                    Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(Employee.LAST_NAME)), value);
                    namePredicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate));
                }
                predicates.add(criteriaBuilder.and(toVarArgs(namePredicates)));
            }

            if (isNull(startPeriod)) {
                startPeriod = LocalDate.now();
            }
            Predicate leaveDateIsNullPredicate = criteriaBuilder.isNull(root.get(Employee.LEAVE_DATE));
            Predicate leaveDateAfterPredicate = criteriaBuilder.greaterThan(root.get(Employee.LEAVE_DATE), startPeriod);
            Predicate leaveDateBetweenPredicate = criteriaBuilder.not(criteriaBuilder.between(root.get(Employee.LEAVE_DATE), startPeriod, LocalDate.now()));
            predicates.add(criteriaBuilder.or(leaveDateIsNullPredicate, leaveDateAfterPredicate, leaveDateBetweenPredicate));

            criteriaQuery.orderBy(Arrays.asList(criteriaBuilder.asc(root.get(Employee.LAST_NAME)),
                    criteriaBuilder.asc(root.get(Employee.FIRST_NAME))));

            Predicate criteria = criteriaBuilder.and(toVarArgs(predicates));
            if (concatenateEmployeeByOfficesAndProjects) {
                Predicate officesOrProjects = criteriaBuilder.or(toVarArgs(orPredicates));
                criteria = criteriaBuilder.and(criteria, officesOrProjects);
            }
            return criteria;
        };
    }

    private boolean isSpecFilter() {
        Collection<String> projectRoles = new HashSet<>();
        projectRoles.add(RoleEnum.PM.getCode());
        projectRoles.add(RoleEnum.USER.getCode());
        return isNotEmpty(roles) && roles.stream().anyMatch(r -> projectRoles.contains(r.getCode())) && isNotEmpty(projectIds);
    }

}
