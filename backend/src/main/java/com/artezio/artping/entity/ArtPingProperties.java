package com.artezio.artping.entity;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Класс для доступа к свойствам приложения из application.properties
 */
@ConfigurationProperties("art-ping")
@Getter
@Setter
public class ArtPingProperties {

    /**
     * Начало обеденного перерыва
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime beginningOfTheLunchBreak;

    /**
     * Окончание обеденного перерыва
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endOfTheLunchBreak;

    /**
     * Время запуска шедулера для обработки всех не отвеченных проверок
     */
    private String processNoResponseRunCron;

    /**
     * Время запуска шедулера для обработки всех не отвеченных проверок
     */
    private String runPlannedTestCron;

    /**
     * Дата запуска добавления уведомлений всем сотрудникам с ролью HR об необходимости обновить календарь
     */
    private String calendarRefreshNotifications;

    /**
     * URL для восстановления пароля
     */
    private String pwdRecoveryURL;

}
