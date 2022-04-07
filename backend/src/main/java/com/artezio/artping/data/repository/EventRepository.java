package com.artezio.artping.data.repository;

import com.artezio.artping.entity.calendar.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для получения данных о выходных днях
 */
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findEventsByCalendarIdInAndDate(Iterable<UUID> calendarIds, LocalDate date);

    List<Event> findEventByCalendarIdAndDateBetween(UUID calendarId, LocalDate startDate, LocalDate endDate);
}
