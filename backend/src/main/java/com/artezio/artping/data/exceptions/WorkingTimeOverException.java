package com.artezio.artping.data.exceptions;

/**
 * Рабочее время завершено
 */
public class WorkingTimeOverException extends ArtPingException {

    public WorkingTimeOverException() {
        super(ExceptionEnum.WORKING_TIME_OVER);
    }
}
