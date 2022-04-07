package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;


public class ParameterExceededException extends ArtPingException {

    /**
     * Параметр {paramName} не может превышать значение {value}
     * @param paramName String
     * @param value Object
     */
    public ParameterExceededException(String paramName, Object value) {
        super(MessageFormat.format(ExceptionEnum.PARAMETER_EXCEEDED.getErrorMessage(), paramName, value));
    }
}
