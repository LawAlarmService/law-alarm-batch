package com.law.alarm.batch.config;

import com.law.alarm.batch.job.SampleJob;
import com.law.alarm.batch.job.ZoneInitJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail zoneInitJobDetail() {
        return newJob(ZoneInitJob.class)
                .withIdentity("zoneInitJobDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail sampleJobDetail() {
        return newJob(SampleJob.class)
                .withIdentity("sampleJobDetail")
                .storeDurably()
                .build();
    }

    // 매일 10:00 분에 실행, 8분 소요.
    @Bean
    public Trigger zoneInitJobTrigger() {
        return newTrigger()
                .forJob(zoneInitJobDetail())
                .withIdentity("zoneInitJobTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(16, 05)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Seoul")))
                .build();
    }

    // 매 분 실행
    @Bean
    public Trigger sampleJobTrigger() {
        return newTrigger()
                .forJob(sampleJobDetail())
                .withIdentity("sampleJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")
                        .inTimeZone(TimeZone.getTimeZone("Asia/Seoul")))
                .build();
    }

}
