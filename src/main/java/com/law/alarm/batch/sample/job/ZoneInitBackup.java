package com.law.alarm.batch.sample.job;

import com.law.alarm.batch.sample.dto.apiResponse.SearchZone;
import com.law.alarm.batch.sample.entity.ZoneEntity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class ZoneInitBackup {

    @Value("${api.domain}")
    private String apiDomain;

    @Value("${api.url.zoneSearch}")
    private String zoneSearchUrl;

    @Bean(name = "zoneInitBackup1")
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        return new JobBuilder("zoneInitJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step(jobRepository, transactionManager, entityManagerFactory))
                .build();
    }

    @Bean(name = "zoneInitStep2")
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        return new StepBuilder("zoneInitStep", jobRepository)
                .<ZoneEntity, String>chunk(10, transactionManager)
                .reader(reader(entityManagerFactory))
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(name = "zoneInitStepByReader2")
    public AbstractItemStreamItemReader<ZoneEntity> reader(EntityManagerFactory entityManagerFactory) {
        return new AbstractItemStreamItemReader<ZoneEntity>() {
            private List<SearchZone.ZoneCd> zoneCdList = new ArrayList<>();
            private int currentIndex = 0;

            @Override
            public ZoneEntity read() throws Exception {
                if (zoneCdList == null) {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiDomain + zoneSearchUrl)
                            .queryParam("areaCd", 11000)
                            .queryParam("uname", "");

                    RestTemplate restTemplate = new RestTemplate();
                    String response = restTemplate.getForObject(builder.toUriString(), String.class);
                    JAXBContext jaxbContext = JAXBContext.newInstance(SearchZone.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    SearchZone searchZone = (SearchZone) unmarshaller.unmarshal(new StringReader(response));
                    zoneCdList = searchZone.getZoneCdList();
                }

                if (currentIndex < zoneCdList.size()) {
                    SearchZone.ZoneCd zoneCd = zoneCdList.get(currentIndex++);
                    ZoneEntity zoneEntity = ZoneEntity.from(zoneCd.getUCODE(), zoneCd.getUNAME(), zoneCd.getLAW_CD(), zoneCd.getLAW_NM());


                    return zoneEntity;
                } else {
                    return null;
                }
            }
        };
    }

    @Bean(name = "zoneInitStepByProcessor2")
    public ItemProcessor<ZoneEntity, String> processor() {
        return zoneCd -> "Processed Data: " + zoneCd.getLawCd();
    }

    @Bean(name = "zoneInitStepByWriter2")
    public ItemWriter<String> writer() {
        return items -> {
            for (String item : items) {
                System.out.println(item);
            }
        };
    }

}
