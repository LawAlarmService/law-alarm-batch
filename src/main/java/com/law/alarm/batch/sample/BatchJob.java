package com.law.alarm.batch.sample;

import com.law.alarm.batch.sample.entity.AreaEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchJob {

    @Bean(name = "sampleJob") // Job: 배치 작업의 논리적 단위로, 여러 개의 Step을 포함
    public Job sampleJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        return new JobBuilder("sampleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sampleStep(jobRepository, transactionManager, entityManagerFactory))
//                .start(sampleStep(jobRepository, platformTransactionManager))
//                    .on("FAILED").to(StepC(jobRepository,transactionManager) // sampleStep의 결과가 FAILED인 경우 StepC로 이동
//                    .on("*").end()  // StepC의 결과와 관계없이 StepC로 이동하면 Flow 종료
//                    .from(StepA(jobRepository,transactionManager) // StepA로부터
//                    .on("*") // sampleStep의 결과가 FAILED 외의 모든 경우에
//                    .to(StepB(jobRepository,transactionManager) // StepB로 이동
//                    .on("*").end() // StepB의 결과와 관계없이 StepB로 이동하면 Flow 종료
//                .end() // Job 종료
                .build();
    }

    @Bean // Step: Job의 실제 작업 단위로, 개별적으로 실행되는 하나의 처리 단위. Reader, Processor, Writer(반복 실행) 혹은 Tasklet(일회성 작업) 으로 구성
    public Step sampleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        return new StepBuilder("step1", jobRepository)
                .<AreaEntity, String>chunk(10, transactionManager)
                .reader(reader(entityManagerFactory))
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public JpaPagingItemReader<AreaEntity> reader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<AreaEntity>()
                .name("areaEntityReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM AreaEntity a")
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<AreaEntity, String> processor() {
        return areaEntity -> "Processed Data: " + areaEntity.getData();
    }

    @Bean
    public ItemWriter<String> writer() {
        return items -> {
            for (String item : items) {
                System.out.println(item);
            }
        };
    }

}
