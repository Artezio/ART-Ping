package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class UserNotFoundByLoginException extends ArtPingException {
    public UserNotFoundByLoginException(String login) {
        super(MessageFormat.format(ExceptionEnum.USER_NOT_FOUND_BY_LOGIN.getErrorMessage(), login));
    }
}
