package com.artezio.artping.config.security.model;

import java.io.Serializable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthenticationResponse implements Serializable {
    private static final long serialVersionUID = 7071919378229360325L;

    private final String jwt;
}
