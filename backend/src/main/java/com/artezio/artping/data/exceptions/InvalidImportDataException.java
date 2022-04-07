package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class InvalidImportDataException extends ArtPingException {

    public InvalidImportDataException(String message) {
        super(MessageFormat.format(ExceptionEnum.INVALID_DATA_IMPORT_EXCEPTION.getErrorMessage(), message));
    }
}
