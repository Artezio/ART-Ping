package com.artezio.artping.data.exceptions;

public enum ExceptionEnum {

    EXCEEDED_CHECKS_PER_DAY("Для сотрудников структурного подразделения превышено "
        + "рекомендованное количество проверок. При необходимости запустите проверку в ручном режиме."),
    NO_EMPLOYEES_FOUND("Сотрудников не найдено"),
    EMPLOYEE_NOT_FOUND_BY_ID("Сотрудник не найден по идентификатору «{0}»"),
    NO_USERS_FOUND("Пользователей не найдено"),
    USER_NOT_FOUND_BY_ID("Пользователь не найден по идентификатору «{0}»"),
    USER_NOT_FOUND_BY_LOGIN("Пользователь не найден по login «{0}»"),
    CALENDAR_NOT_FOUND_BY_ID("Календарь не найден по идентификатору «{0}»"),
    CALENDAR_HAS_DEPENDENCIES("На данный календарь «{0}» ссылаются сотрудники, либо офисы"),
    OFFICE_HAS_EMPLOYEES("В данном офисе «{0}» содержатся сотрудники"),
    EVENT_NOT_FOUND_BY_ID("Выходной не найден по идентификатору «{0}»"),
    NO_OFFICES_FOUND("Офисов не найдено"),
    NO_PROJECTS_FOUND("Проектов не найдено"),
    WORKING_TIME_OVER("Рабочее время завершено"),
    USER_WITH_THIS_LOGIN_ALREADY_EXIST("Пользователь с таким логином уже существует в системе"),
    EMPLOYEE_WITH_THIS_EMAIL_ALREADY_EXIST("Сотрудник с таким email уже существует в системе"),
    NO_REFERENCE_EXCEPTION("Запрошенный справочник «{0}» не найден"),
    PARAMETER_EXCEEDED("Параметр «{0}» не может превышать значение {1}"),
    INVALID_DATA_IMPORT_EXCEPTION("Возникла ошибка при импорте: {0}"),
    READ_IMPORT_FILE("Ошибка при чтении файла: {0}"),
    CALENDAR_WITH_THIS_NAME_ALREADY_EXIST("Календарь с таким названием уже существует"),
    OFFICE_WITH_THIS_NAME_ALREADY_EXIST("Офис с таким названием уже существует"),
    PROJECT_WITH_THIS_NAME_ALREADY_EXIST("Проект с таким названием уже существует"),
    NOTIFICATION_NOT_FOUND("Запрашиваемое уведомление не найдено"),
    PASSWORD_RECOVERY_REQUEST_IS_NOT_ACTIVE_EXCEPTION("Ссылка на восстановление пароля неактивна"),
    ;


    private final String errorMessage;

    ExceptionEnum(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
