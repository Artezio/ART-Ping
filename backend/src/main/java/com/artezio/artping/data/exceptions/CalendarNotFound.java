package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class CalendarNotFound extends ArtPingException {

    public CalendarNotFound(String id) {
        super(MessageFormat.format(ExceptionEnum.CALENDAR_NOT_FOUND_BY_ID.getErrorMessage(), id));
    }
}
