package com.artezio.artping.data.repository;

import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

/**
 * Репозиторий для работы с сотрудниками
 */
public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {

    List<Employee> findAllByBaseOfficeId(UUID officeId);

    @Query("select e.login from Employee e")
    List<String> getAllEmployeesLogin();

    /**
     * Нахождение сотрудника по его email
     *
     * @param email почта сотрудника
     * @return сотрудник
     */
    Optional<Employee> findOptionalByEmailIgnoreCase(String email);

    /**
     * Получение всех email сотрудников
     *
     * @return список всех email сотрудников
     */
    @Query("select e.email from Employee e")
    List<String> getAllEmployeeEmails();

    /**
     * Поиск всех актуальных сотрудников
     * <p/>
     *
     * @return список сотрудников, у которых связанный пользователь активен
     */
    @Query("select e from Employee e inner join e.user u where u.active = true")
    Collection<Employee> findAllActual();

    /**
     * Поиск не уволенных сотрудников с заданной почтой
     */
    Collection<Employee> findAllByUserActiveTrueAndEmailIgnoreCase(String email);

    /**
     * Поиск не уволенных сотрудников по логину
     *
     * @param login логин сотрудника
     * @return найденный сотрудник
     */
    Employee findDistinctByUserActiveTrueAndLogin(String login);

    /**
     * Получение списка активных сотрудников с определенной ролью
     *
     * @param role роль сотрудника
     * @return список сотрудников по выбранной роли
     */
    List<Employee> findAllByUserActiveTrueAndRoles(Role role);

}
