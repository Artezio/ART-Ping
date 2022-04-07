package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.NO_USERS_FOUND;

public class NoSuchUserException extends ArtPingException {
    public NoSuchUserException() {
        super(NO_USERS_FOUND);
    }
}
