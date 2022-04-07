package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.CALENDAR_WITH_THIS_NAME_ALREADY_EXIST;

public class CalendarWithThisNameAlreadyExist extends ArtPingException {
    public CalendarWithThisNameAlreadyExist() {
        super(CALENDAR_WITH_THIS_NAME_ALREADY_EXIST);
    }
}