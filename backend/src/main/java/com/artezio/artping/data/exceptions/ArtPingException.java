package com.artezio.artping.data.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtPingException extends RuntimeException {

    private ExceptionEnum exception;

    public ArtPingException(ExceptionEnum exception) {
        super(exception.getErrorMessage());
        this.exception = exception;
    }

    public ArtPingException(String message) {
        super(message);
    }
}
