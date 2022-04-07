package com.artezio.artping.entity.calendar;

/**
 * Тип события
 */
public enum DayType {
    PAID("PAID"),
    UNPAID("UNPAID"),
    WEEKEND("Выходной"),
    SICKNESS("Больничный"),
    SICK("SICK"),
    VACATION("Отпуск"),
    WORKING_DAY("Рабочий"),
    HOLIDAY("Праздничный день");

    private final String value;

    DayType(String value) {
        this.value = value;
    }

    public static DayType getDayType(String name) {
        for (DayType type : DayType.values()) {
            if (type.value.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
