package com.artezio.artping.entity;

/**
 * Статус уведомления о проверке
 */
public enum NotificationStatusEnum {
    NOT_SENT,
    SENT,
    RECEIVED,
    NO_SUBSCRIPTION;

    NotificationStatusEnum() {
    }
}
