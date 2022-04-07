package com.artezio.artping.data.repository;

import com.artezio.artping.data.repository.ext.EmployeeTestExtSearch;
import com.artezio.artping.entity.EmployeeTest;
import com.artezio.artping.entity.TestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с провероками сотрудника
 */
@Repository
public interface EmployeeTestRepository extends JpaRepository<EmployeeTest, UUID>, PagingAndSortingRepository<EmployeeTest, UUID>, EmployeeTestExtSearch {

    /**
     * Получение всех проверок сотрудника
     *
     * @param employeeId идентификатор сотрудника
     * @return список проверок
     */
    List<EmployeeTest> findAllByEmployeeId(UUID employeeId);

    /**
     * Получение проверок определенного статуса начиная с указанного момента времени
     *
     * @param status    статус проверки
     * @param startTime момент времени
     * @return список проверок
     */
    List<EmployeeTest> findByStatusAndStartTimeBefore(TestStatusEnum status, ZonedDateTime startTime);

    /**
     * Получение проверок сотрудника с указанного момента времени в запрашиваемом статусе
     *
     * @param status     статус проверки
     * @param startTime  момент времени
     * @param employeeId идентификатор сотрудника
     * @return список проверок
     */
    List<EmployeeTest> findAllByStatusAndStartTimeAfterAndEmployeeId(TestStatusEnum status, ZonedDateTime startTime, UUID employeeId);

    /**
     * Получение проверок сотрудника в определенном статусе
     *
     * @param statusEnum статус проверки
     * @param employeeId идентификатор сотрудника
     * @return список проверок
     */
    List<EmployeeTest> findAllByStatusAndEmployeeId(TestStatusEnum statusEnum, UUID employeeId);

    /**
     * Получение проверок с указанного момента в определенном статусе и с не указанным временем ответа
     *
     * @param status    статус проверки
     * @param startTime момент времени
     * @return список проверок
     */
    List<EmployeeTest> findByStatusAndResponseTimeIsNullAndStartTimeBefore(TestStatusEnum status, ZonedDateTime startTime);

    /**
     * Получение проверок сотрудников в определенном статусе
     *
     * @param statusEnum статус проверок
     * @param ids        идентификаторы сотрудников
     * @return список проверок
     */
    List<EmployeeTest> findAllByStatusAndEmployeeIdIn(TestStatusEnum statusEnum, Iterable<UUID> ids);

    /**
     * Количество проверок сотрудника за определенный период
     *
     * @param employeeId идентификатор сотрудника
     * @param start      начало периода
     * @param end        конец периода
     * @return количество проверок
     */
    @Query("select count(et.id) as cnt from EmployeeTest et where et.employee.id = :employeeId and (et.startTime between :start and :end) and et.status <> 'CANCELED'")
    Long countEmployeeTestInPeriod(@Param("employeeId") UUID employeeId,
                                   @Param("start") ZonedDateTime start,
                                   @Param("end") ZonedDateTime end);
}