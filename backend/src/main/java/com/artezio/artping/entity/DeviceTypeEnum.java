package com.artezio.artping.entity;

import java.util.Arrays;

/**
 * Тип устройства с которого сотрудник заходит в систему
 */
public enum DeviceTypeEnum {

    MOBILE("Mobile Phone", "Tablet"),
    PC("Desktop"),
    UNDEFINED("Undefined");

    private final String[] names;

    DeviceTypeEnum(String... names) {
        this.names = names;
    }

    public static DeviceTypeEnum fromName(String name) {
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            if (Arrays.asList(deviceType.names).contains(name)) {
                return deviceType;
            }
        }
        return UNDEFINED;
    }
}