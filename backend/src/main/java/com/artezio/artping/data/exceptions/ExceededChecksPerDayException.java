package com.artezio.artping.data.exceptions;

/**
 * Превышено количество проверок в день
 */
public class ExceededChecksPerDayException extends ArtPingException {

    public ExceededChecksPerDayException() {
        super(ExceptionEnum.EXCEEDED_CHECKS_PER_DAY);
    }
}
