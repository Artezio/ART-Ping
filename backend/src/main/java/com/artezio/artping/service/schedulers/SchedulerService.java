package com.artezio.artping.service.schedulers;

import com.artezio.artping.service.schedulers.job.ProcessTestsWithoutResponseJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Шедулер для обработки неотвеченных сообщений, у которых с момента запуска прошло больше времени,
 * чем установленно в системном параметре (60 минут)
 */
@Slf4j
@Service
public class SchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    @PostConstruct
    public void init() {
        this.schedulerFactory.start();
    }

    /**
     * Создание планировки, которая отметит проверку переданных сотрудников
     * как не пройденную после определённого времени
     *
     * @param employeeIdsForTest идентификаторы проверяемых сотрудников
     * @param dateToStart        дата начала проверки
     */
    public void scheduleProcessingTestsWithNoResponseJob(List<UUID> employeeIdsForTest, Date dateToStart) {
        if (employeeIdsForTest == null || employeeIdsForTest.isEmpty()) {
            return;
        }
        String jobName = dateToStart.toString().concat(employeeIdsForTest.toString());
        log.info("ScheduleOnceRunningJob with name = ".concat(jobName));
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(ProcessTestsWithoutResponseJob.EMPLOYEE_IDS_KEY, employeeIdsForTest);
        JobDetail job = JobBuilder
                .newJob(ProcessTestsWithoutResponseJob.class)
                .usingJobData(new JobDataMap(dataMap))
                .withIdentity(jobName)
                .build();

        scheduleOnceRunningJob(jobName, job, dateToStart);
    }

    private void scheduleOnceRunningJob(String name, JobDetail job, Date dateToStart) {
        Scheduler scheduler = schedulerFactory.getScheduler();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .startAt(dateToStart)
                .withIdentity(name)
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule())
                .build();
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Exception trying to schedule job. ", e);
        }
    }

}
