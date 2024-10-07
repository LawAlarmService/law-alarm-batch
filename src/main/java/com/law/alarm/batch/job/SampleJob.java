package com.law.alarm.batch.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SampleJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("SampleJob 실행 시작");
        try {
            System.out.println("SampleJob 로직");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
        System.out.println("SampleJob 실행 완료");
    }

}
