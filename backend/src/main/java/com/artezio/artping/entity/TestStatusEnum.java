package com.artezio.artping.entity;

/**
 * Статусы проверок
 */
public enum TestStatusEnum {

    IN_PROGRESS("in_progress", "In Progress"),
    SUCCESS("success", "Success"),
    NOT_SUCCESS("not_success", "Not success"),
    NO_RESPONSE("no_response", "No response"),
    CANCELED("canceled", "Canceled"),
    PLANNED("planned", "Planned");

    private final String code;
    private final String name;

    TestStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
