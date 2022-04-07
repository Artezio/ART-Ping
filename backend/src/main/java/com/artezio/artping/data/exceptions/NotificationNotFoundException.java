package com.artezio.artping.data.exceptions;

public class NotificationNotFoundException extends ArtPingException {
    public NotificationNotFoundException() {
        super(ExceptionEnum.NOTIFICATION_NOT_FOUND);
    }
}
