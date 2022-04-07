package com.artezio.artping.data.exceptions;

/**
 * Сотрудников не найдено
 */
public class NoProjectsFoundException extends ArtPingException {

    public NoProjectsFoundException() {
        super(ExceptionEnum.NO_PROJECTS_FOUND);
    }
}
