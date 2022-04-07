package com.artezio.artping.data.exceptions;

/**
 * Сотрудников не найдено
 */
public class NoEmployeesFoundException extends ArtPingException {

    public NoEmployeesFoundException() {
        super(ExceptionEnum.NO_EMPLOYEES_FOUND);
    }
}
