package com.artezio.artping.utils;

import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.service.utils.DateUtils;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.artezio.artping.service.utils.DateUtils.getWeekends;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DateUtilsTest {

    static Collection<DayOfWeek> resNotNull, resNull, resFull;

    @Mock
    private Employee employee;

    @BeforeAll
    static void setUp() {
        resFull = DateUtils.getWeekends("0;1;2;3;4;5;6;0;1;2;3");
        resNotNull = DateUtils.getWeekends("0;6");
        resNull = DateUtils.getWeekends((String) null);
    }

    @Test
    void testNotNull() {
        assertNotNull(resNotNull);
        assertNotNull(resFull);
    }

    @Test
    void testSize() {
        assertEquals(2, resNotNull.size());
        assertEquals(7, resFull.size());
    }

    @Test
    void testValues() {
        assertTrue(resNotNull.contains(DayOfWeek.SATURDAY));
        assertTrue(resNotNull.contains(DayOfWeek.SUNDAY));

        assertTrue(resFull.contains(DayOfWeek.MONDAY));
        assertTrue(resFull.contains(DayOfWeek.TUESDAY));
        assertTrue(resFull.contains(DayOfWeek.WEDNESDAY));
        assertTrue(resFull.contains(DayOfWeek.THURSDAY));
        assertTrue(resFull.contains(DayOfWeek.FRIDAY));
        assertTrue(resFull.contains(DayOfWeek.SATURDAY));
        assertTrue(resFull.contains(DayOfWeek.SUNDAY));
    }

    @Test
    void testNull() {
        assertNotNull(resNull);
    }

    @Test
    void testSizeNull() {
        assertEquals(0, resNull.size());
    }

    @Test
    void testException() {
        assertThrows(DateTimeException.class, () -> DateUtils.getWeekends("8"));
    }

    @Test
    void testWeekends() {
        when(employee.getCalendar()).thenReturn(mock(Calendar.class));
        when(employee.getCalendar().getWeekendDays()).thenReturn("0;6");

        Collection<LocalDate> result = getWeekends(
                employee,
                LocalDate.of(2021, Month.JANUARY, 1),
                LocalDate.of(2021, Month.JANUARY, 7)
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(resFull.contains(DayOfWeek.SATURDAY));
        assertTrue(resFull.contains(DayOfWeek.SUNDAY));
    }

}
