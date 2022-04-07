package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;
import java.util.UUID;

public class CalendarHasDependenciesException extends ArtPingException {

    public CalendarHasDependenciesException(String name) {
        super(MessageFormat.format(ExceptionEnum.CALENDAR_HAS_DEPENDENCIES.getErrorMessage(), name));
    }
}
