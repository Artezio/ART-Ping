package com.artezio.artping.service.schedulers;

import com.artezio.artping.entity.NotificationType;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.service.EmployeeService;
import com.artezio.artping.service.EmployeeTestService;
import com.artezio.artping.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Шедулеры
 */
@Slf4j
@Service
public class CronScheduler {

    @Autowired
    private EmployeeTestService employeeTestService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private NotificationService notificationService;

    /**
     * Обновление статуса для всех не отвеченных проверок
     */
    @Scheduled(cron = "${art-ping.processNoResponseRunCron}")
    public void runProcessingTestsWithNoResponseJob() {
        log.info("Start the scheduling job runProcessingTestsWithNoResponseJob at " + LocalDateTime.now());
        employeeTestService.processEmployeeAllTestsWithNoResponse();
    }

    /**
     * Запуск запланированных проверок
     */
    @Scheduled(cron = "${art-ping.runPlannedTestCron}")
    public void runPlannedTest() {
        log.info("Start the scheduling job runPlannedTest at " + LocalDateTime.now());
        employeeTestService.runPlannedTest();
        log.info("Start the scheduling job processPlannedTestsWithNoResponse at " + LocalDateTime.now());
        employeeTestService.processPlannedTestsWithNoResponse();
    }

    /**
     * Создание напоминания сотрудникам с ролью HR о необходимости обновить календари
     */
    @Scheduled(cron = "${art-ping.calendarRefreshNotifications}")
    public void runCalendarRefreshingNotification() {
        log.info("Start the scheduling job runCalendarRefreshingNotification at " + LocalDateTime.now());
        List<Employee> hrEmployees = employeeService.getAllHREmployees();
        notificationService.createNotificationsToEmployees("Пожалуйста заполните календарь на следующий год",
                hrEmployees, NotificationType.REFRESH_CALENDAR);
        log.info("CalendarRefreshingNotifications has successfully created at " + LocalDateTime.now());
    }

}
