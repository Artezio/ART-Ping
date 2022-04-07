package com.artezio.artping.service.utils;

import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.util.Objects.nonNull;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;


public class LocalTimeUtils {

    private static final int MINUTES_PER_HOUR = 60;

    private static final Random RANDOM = new Random();

    /**
     * Метод генерирует рандомное время в заданном промежутке с шагом в 5 минут
     * @param startTime начало временного промежутка
     * @param endTime окончание временного промежутка включительно
     * @return рандомное время
     */
    public static LocalTime randomInARange(LocalTime startTime, LocalTime endTime) {
        int startMinuteOfDay = toMinuteOfDay(startTime);
        int endMinuteOfDay = toMinuteOfDay(endTime);
        int randomMinuteOfDay = startMinuteOfDay + RANDOM.nextInt(((endMinuteOfDay - startMinuteOfDay)/ 5 + 1)) * 5;
        return ofMinuteOfDay(randomMinuteOfDay);
    }

    public static LocalTime getMaxFrom(LocalTime... times) {
        if (nonNull(times) && times.length != 0) {
            return getMaxFrom(Arrays.asList(times));
        }

        throw new IllegalArgumentException();
    }

    public static LocalTime getMaxFrom(List<LocalTime> list) {
        if (nonNull(list)) {
            List<LocalTime> listWithoutNulls = list.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!listWithoutNulls.isEmpty()) {
                listWithoutNulls.sort(LocalTime::compareTo);
                return listWithoutNulls.get(listWithoutNulls.size() - 1);
            }
        }

        throw new IllegalArgumentException();
    }

    private static int toMinuteOfDay(LocalTime localTime) {
        int total = localTime.getHour() * MINUTES_PER_HOUR;
        total += localTime.getMinute();
        return total;
    }

    private static LocalTime ofMinuteOfDay(int minuteOfDay) {
        MINUTE_OF_DAY.checkValidValue(minuteOfDay);
        int hours = minuteOfDay / MINUTES_PER_HOUR;
        minuteOfDay -= hours * MINUTES_PER_HOUR;
        return LocalTime.of(hours, minuteOfDay, 0, 0);
    }
}
