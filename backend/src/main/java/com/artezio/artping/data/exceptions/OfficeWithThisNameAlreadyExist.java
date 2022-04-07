package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.OFFICE_WITH_THIS_NAME_ALREADY_EXIST;

public class OfficeWithThisNameAlreadyExist extends ArtPingException {
    public OfficeWithThisNameAlreadyExist() {
        super(OFFICE_WITH_THIS_NAME_ALREADY_EXIST);
    }
}
