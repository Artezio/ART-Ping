package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

public class NoReferencesException extends ArtPingException {
    public NoReferencesException(String key) {
        super(MessageFormat.format(ExceptionEnum.NO_REFERENCE_EXCEPTION.getErrorMessage(), key));
    }
}

