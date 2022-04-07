package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class EmployeeNotFoundByIdException extends ArtPingException {

    public EmployeeNotFoundByIdException(String id) {
        super(MessageFormat.format(ExceptionEnum.EMPLOYEE_NOT_FOUND_BY_ID.getErrorMessage(), id));
    }
}
