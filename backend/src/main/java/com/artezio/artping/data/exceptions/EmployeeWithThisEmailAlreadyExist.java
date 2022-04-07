package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.EMPLOYEE_WITH_THIS_EMAIL_ALREADY_EXIST;

public class EmployeeWithThisEmailAlreadyExist extends ArtPingException {

    public EmployeeWithThisEmailAlreadyExist() {
        super(EMPLOYEE_WITH_THIS_EMAIL_ALREADY_EXIST);
    }
}
