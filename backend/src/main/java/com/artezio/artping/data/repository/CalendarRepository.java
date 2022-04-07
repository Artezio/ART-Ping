package com.artezio.artping.data.repository;

import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.office.Office;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для получения данных о календарях
 */
public interface CalendarRepository extends JpaRepository<Calendar, UUID> {

    /**
     * Получение коллекции всех активных офисных календарей
     */
    Collection<Calendar> findAllByActiveTrueAndCustomCalendarFalse();

    /**
     * Поиск по вхождению в название офиса
     *
     * @param searchString строка поиска
     * @param pageable     информация о странице
     * @return коллекция найденных офисов
     */
    Page<Calendar> findAllByActiveTrueAndCustomCalendarFalseAndNameContainingIgnoreCase(String searchString,
                                                                                        Pageable pageable);

    /**
     * Поиск по вхождению в название офиса
     *
     * @param searchString строка поиска
     * @param offices      ограничивающая коллекция офисов
     * @param pageable     информация о странице
     * @return коллекция найденных офисов
     */
    Page<Calendar> findAllByActiveTrueAndCustomCalendarFalseAndNameContainingIgnoreCaseAndOfficesIn(String searchString,
                                                                                                    Collection<Office> offices,
                                                                                                    Pageable pageable);

    /**
     * Определение, есть ли у календаря связь с офисом или сотрудником
     *
     * @param calendarId идентификатор календаря
     * @return true, если связь есть
     */
    @Query(" select case when count(c) > 0 then true else false end " +
            " from Calendar c " +
            " where c.id = :calendarId " +
            " and (c.offices is not empty or c.employees is not empty) " +
            " and c.active = true ")
    boolean hasLinkedOfficesOrEmployees(UUID calendarId);

    /**
     * Поиск календаря по идентификатору офиса
     *
     * @param officeId идентификатор офиса
     * @return Найденный календарь
     */
    @Query(" select c " +
            " from Calendar c inner join Office o on o.calendar = c " +
            " where o.id = :officeId" +
            " and c.active = true ")
    Calendar findByOfficeId(UUID officeId);

    /**
     * Поиск активного календаря по идентификатору
     *
     * @param calendarId идентификатор каллендаря
     * @return найденный календарь
     */
    Optional<Calendar> findByIdAndActiveTrue(UUID calendarId);

    /**
     * Поиск календаря по нахзванию
     *
     * @param name название календаря
     * @return найденный календарь
     */
    Optional<Calendar> findByNameIgnoreCase(String name);

    /**
     * Получение всех наименований календарей
     * @return список наименований календарей
     */
    @Query("select c.name from Calendar c")
    List<String> findAllCalendarNames();

}
