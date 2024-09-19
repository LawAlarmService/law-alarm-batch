package com.law.alarm.batch.sample.job;

import com.law.alarm.batch.sample.dto.apiResponse.SearchZone;
import com.law.alarm.batch.sample.entity.AreaEntity;
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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;

@Configuration
@EnableBatchProcessing
public class ZoneInit {

    @Value("${api.domain}")
    private String apiDomain;

    @Value("${api.url.zoneSearch}")
    private String zoneSearchUrl;

    @Bean(name = "zoneInitJob")
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        return new JobBuilder("zoneInitJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step(jobRepository, transactionManager, entityManagerFactory))
                .build();
    }

    @Bean(name = "zoneInitStep")
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        return new StepBuilder("zoneInitStep", jobRepository)
                .<AreaEntity, ZoneEntity>chunk(100, transactionManager)
                .reader(reader(entityManagerFactory))
                .processor(processor())
                .writer(writer(entityManagerFactory))
                .build();
    }

    @Bean(name = "zoneInitStepByReader")
    public JpaPagingItemReader<AreaEntity> reader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<AreaEntity>()
                .name("areaEntityReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM AreaEntity a")
                .pageSize(100)
                .build();
    }

    @Bean(name = "zoneInitStepByProcessor")
    public ItemProcessor<AreaEntity, ZoneEntity> processor() {
        return areaEntity -> {

            System.out.println("areaEntity : " + areaEntity);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiDomain + zoneSearchUrl)
                    .queryParam("areaCd", areaEntity.getAreaCd())
                    .queryParam("uname", "");

            RestTemplate restTemplate = new RestTemplate();

            System.out.println("builder.toUriString() : " + builder.toUriString());

            String response = restTemplate.getForObject(builder.toUriString(), String.class);
            JAXBContext jaxbContext = JAXBContext.newInstance(SearchZone.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SearchZone searchZone = (SearchZone) unmarshaller.unmarshal(new StringReader(response));

            SearchZone.ZoneCd zoneCd = searchZone.getZoneCdList().get(0);
            ZoneEntity zoneEntity = ZoneEntity.from(zoneCd.getUCODE(),zoneCd.getUNAME(),zoneCd.getLAW_CD(), zoneCd.getLAW_NM());

            System.out.println("zoneEntity : " + zoneEntity);

            return zoneEntity;
        };
    }

    @Bean(name = "zoneInitStepByWriter")
    public ItemWriter<ZoneEntity> writer(EntityManagerFactory entityManagerFactory) {
        return items -> {

            System.out.println("items.size() : " + items.size());

            for (ZoneEntity item : items) {
                entityManagerFactory.createEntityManager().merge(item);
            }
        };
    }

}
