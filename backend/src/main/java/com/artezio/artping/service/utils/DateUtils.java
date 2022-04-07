package com.artezio.artping.service.utils;

import com.artezio.artping.entity.user.Employee;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * Поиск автоматических выходных дней по указанному соотруднику для интервала дат
     *
     * @param employee сотрудник
     * @param startDay первый день интервала
     * @param endDay   последний день интервала
     *
     * @return коллекция дней
     */
    public static Collection<LocalDate> getWeekends(Employee employee, LocalDate startDay, LocalDate endDay) {
        if (employee == null) {
            return emptySet();
        }

        com.artezio.artping.entity.calendar.Calendar calendar = employee.getCalendar() != null ? employee.getCalendar() : employee.getBaseOffice().getCalendar();
        Collection<LocalDate> result = new HashSet<>();

        final Collection<DayOfWeek> weekends = getWeekends(calendar.getWeekendDays());

        LocalDate currentDay = startDay;
        while (!currentDay.isAfter(endDay)) {
            if (weekends.contains(currentDay.getDayOfWeek())) {
                result.add(currentDay);
            }
            currentDay = currentDay.plusDays(1);
        }

        return result;
    }

    /**
     * Конвертация маски выходных дней в перечистелние
     *
     * @param weekendDays маска выходных дней календаря
     *
     * @return коллекция выходных дней календаря
     */
    public static Collection<DayOfWeek> getWeekends(String weekendDays) {
        if (isBlank(weekendDays)) {
            return emptySet();
        }

        // пока у нас с ГУИ приходит 0 в качестве константы воскерсенья
        weekendDays = weekendDays.replace("0", "7");

        Collection<DayOfWeek> result = new HashSet<>();
        for (String str : weekendDays.split(";")) {
            result.add(DayOfWeek.of(Integer.parseInt(str)));
        }

        return result;
    }

    /**
     * Конвертация маски выходных дней в перечистелние для указанного сотрудника
     *
     * @param employee сотрудник
     *
     * @return коллекция выходных дней календаря
     */
    public static Collection<DayOfWeek> getWeekends(Employee employee) {
        com.artezio.artping.entity.calendar.Calendar calendar = employee.getCalendar() != null ?
                employee.getCalendar() : employee.getBaseOffice().getCalendar();

        return getWeekends(calendar.getWeekendDays());
    }

}
