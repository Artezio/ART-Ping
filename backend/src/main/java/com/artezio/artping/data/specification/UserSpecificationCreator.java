package com.artezio.artping.data.specification;

import com.artezio.artping.entity.AbstractEntity;
import com.artezio.artping.entity.project.AbstractProjectEmployee;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

/**
 * Спецификация для критирия поиска пользователей
 */
@Getter
@Setter
public class UserSpecificationCreator extends AbstractSpecificationCreator<Employee> {

    public static final int MIN_LENGTH_SEARCH_LINE = 3;


    private String nameOrLogin;

    private Set<String> roleIds;

    private Set<String> officeIds;

    private Set<String> projectIds;


    @Override
    public Specification<Employee> create() {
        return (root, cq, cb) -> {

            addNameOrLoginPredicate(root, cb);
            addRoleIdsPredicate(root, cq, cb);
            addOfficeIdsPredicate(root, cb);
            addProjectIdsPredicate(root, cq, cb);

            return cb.and(toVarArgs(allPredicates));
        };
    }


    private void addProjectIdsPredicate(Root<Employee> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (!CollectionUtils.isEmpty(projectIds)) {
            Subquery<AbstractProjectEmployee> subQuery = cq.subquery(AbstractProjectEmployee.class);
            Root<AbstractProjectEmployee> projectEmployeeRoot = subQuery.from(AbstractProjectEmployee.class);

            Predicate predicate = cb.and(
                    cb.equal(projectEmployeeRoot.get(AbstractProjectEmployee.EMPLOYEE_ID), root.get(AbstractEntity.ID)),
                    in(cb, projectEmployeeRoot.get(AbstractProjectEmployee.PROJECT_ID), projectIds)
            );

            subQuery.select(projectEmployeeRoot).where(predicate);

            allPredicates.add(cb.exists(subQuery));
        }
    }

    private void addOfficeIdsPredicate(Root<Employee> root, CriteriaBuilder cb) {
        if (!officeIds.isEmpty()) {
                Join<Office, Employee> join = root.join(Employee.BASE_OFFICE);
            allPredicates.add(in(cb, join.get(AbstractEntity.ID), officeIds));
        }
    }

    private void addRoleIdsPredicate(Root<Employee> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (!roleIds.isEmpty()) {
            Subquery<Role> subQuery = cq.subquery(Role.class);
            Root<Role> roleRoot = subQuery.from(Role.class);

            Expression<Collection<Role>> ownerRoles = root.get(Employee.ROLES);

            Predicate predicate = cb.and(
                    in(cb, roleRoot.get(AbstractEntity.ID), roleIds),
                    cb.isMember(roleRoot, ownerRoles)
            );


            subQuery.select(roleRoot).where(predicate);

            allPredicates.add(cb.exists(subQuery));
        }
    }

    private void addNameOrLoginPredicate(Root<Employee> root, CriteriaBuilder cb) {
        if (!nameOrLogin.isEmpty() && nameOrLogin.length() > MIN_LENGTH_SEARCH_LINE) {
            List<Predicate> nameOrLoginPredicates = new ArrayList<>();
            String[] characterGroups = nameOrLogin.split(" ", 2);
            for (String charGroup : characterGroups) {
                List<Predicate> predicateList = new ArrayList<>();

                String value = prepareForLike(charGroup.toLowerCase());
                Predicate firstNamePredicate = cb.like(cb.lower(root.get(Employee.FIRST_NAME)), value);
                Predicate lastNamePredicate = cb.like(cb.lower(root.get(Employee.LAST_NAME)), value);

                predicateList.add(firstNamePredicate);
                predicateList.add(lastNamePredicate);

                if (characterGroups.length == 1) {
                    Predicate loginPredicate = cb.like(cb.lower(root.get(Employee.LOGIN)), value);
                    predicateList.add(loginPredicate);
                }

                nameOrLoginPredicates.add(cb.or(predicateList.toArray(new Predicate[0])));
            }

            allPredicates.add(cb.and(toVarArgs(nameOrLoginPredicates)));
        }
    }
}
