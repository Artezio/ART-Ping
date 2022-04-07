package com.artezio.artping.data.repository.ext;

import com.artezio.artping.data.repository.EmployeeTestRepository;
import com.artezio.artping.entity.EmployeeTest;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Расширение для {@link EmployeeTestRepository} для реализации поиска через критерию
 */
public interface EmployeeTestExtSearch {

    /**
     * Поиск тестов за выбранный период
     *
     * @param id        идентификатор сотрудника
     * @param startDate дата начала периода
     * @param endDate   дата конца периода
     *
     * @return коллекция найденных тестов
     */
    Collection<EmployeeTest> findForPeriod(UUID id, ZonedDateTime startDate, ZonedDateTime endDate);

}
