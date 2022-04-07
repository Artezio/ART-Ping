package com.artezio.artping.data.exceptions;

/**
 * Сотрудников не найдено
 */
public class NoOfficesFoundException extends ArtPingException {

    public NoOfficesFoundException() {
        super(ExceptionEnum.NO_OFFICES_FOUND);
    }
}
