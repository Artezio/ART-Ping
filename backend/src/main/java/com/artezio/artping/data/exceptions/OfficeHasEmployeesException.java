package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class OfficeHasEmployeesException extends ArtPingException {
    public OfficeHasEmployeesException(String name) {
        super(MessageFormat.format(ExceptionEnum.OFFICE_HAS_EMPLOYEES.getErrorMessage(), name));
    }
}
