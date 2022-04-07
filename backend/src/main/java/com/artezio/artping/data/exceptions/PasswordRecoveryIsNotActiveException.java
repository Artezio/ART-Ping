package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.PASSWORD_RECOVERY_REQUEST_IS_NOT_ACTIVE_EXCEPTION;

public class PasswordRecoveryIsNotActiveException extends ArtPingException {
    public PasswordRecoveryIsNotActiveException() {
        super(PASSWORD_RECOVERY_REQUEST_IS_NOT_ACTIVE_EXCEPTION);
    }
}

