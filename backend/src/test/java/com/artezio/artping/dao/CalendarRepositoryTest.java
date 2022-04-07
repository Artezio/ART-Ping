package com.artezio.artping.dao;

import com.artezio.artping.data.repository.CalendarRepository;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.calendar.DayType;
import com.artezio.artping.entity.calendar.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CalendarRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CalendarRepository calendarRepository;

    @Test
    void testCreate() {
        Calendar calendar = createCalendar();
        Event e = new Event();
        e.setCalendar(calendar);
        e.setDate(LocalDate.now());
        e.setDescription("description");
        e.setType(DayType.VACATION);
        calendar.getEvents().add(e);

        calendar = calendarRepository.save(calendar);
        assertNotNull(calendar);
        assertNotNull(calendar.getId());
        assertNotNull(e);
        assertNotNull(e.getId());
    }

}
