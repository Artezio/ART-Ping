package com.artezio.artping.dto;

/**
 * Названия ролей
 */
public enum RoleEnum {

    ADMIN("ADMIN"),
    DIRECTOR("DIRECTOR"),
    PM("PM"),
    HR("HR"),
    USER("USER");

    private final String code;

    RoleEnum(String code)
    {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RoleEnum fromCode(String name) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.code.equals(name)) return role;
        }
        throw new IllegalArgumentException();
    }
}
