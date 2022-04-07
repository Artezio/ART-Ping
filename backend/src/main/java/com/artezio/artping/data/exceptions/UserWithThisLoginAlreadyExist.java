package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.USER_WITH_THIS_LOGIN_ALREADY_EXIST;

public class UserWithThisLoginAlreadyExist extends ArtPingException {
    public UserWithThisLoginAlreadyExist() {
        super(USER_WITH_THIS_LOGIN_ALREADY_EXIST);
    }
}
