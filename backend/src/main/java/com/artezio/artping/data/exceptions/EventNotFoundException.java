package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class EventNotFoundException extends ArtPingException {
    public EventNotFoundException(String id) {
        super(MessageFormat.format(ExceptionEnum.EVENT_NOT_FOUND_BY_ID.getErrorMessage(), id));
    }
}
