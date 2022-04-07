package com.artezio.artping.service.schedulers.job;

import com.artezio.artping.service.EmployeeTestService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProcessTestsWithoutResponseJob implements Job {

    public final static String EMPLOYEE_IDS_KEY = "employeeIdsKey";

    @Autowired
    private EmployeeTestService employeeTestService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        List<UUID> employeeIds = (List<UUID>) dataMap.get(EMPLOYEE_IDS_KEY);
        employeeTestService.processInProgressTestsWithNoResponse(employeeIds);
    }
}
