package com.artezio.artping.data.exceptions;

import static com.artezio.artping.data.exceptions.ExceptionEnum.PROJECT_WITH_THIS_NAME_ALREADY_EXIST;


public class ProjectWithThisNameAlreadyExist extends ArtPingException {
    public ProjectWithThisNameAlreadyExist() {
        super(PROJECT_WITH_THIS_NAME_ALREADY_EXIST);
    }
}
