package com.artezio.artping.config.security.model;

import java.io.Serializable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthenticationRequest implements Serializable {
    private static final long serialVersionUID = 3338250670696801015L;

    private String login;
    private String password;
}
