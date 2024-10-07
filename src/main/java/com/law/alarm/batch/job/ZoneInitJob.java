package com.law.alarm.batch.job;

import com.law.alarm.batch.dto.apiResponse.SearchZone;
import com.law.alarm.batch.entity.AreaEntity;
import com.law.alarm.batch.entity.ZoneEntity;
import com.law.alarm.batch.repository.AreaEntityRepository;
import com.law.alarm.batch.repository.ZoneEntityRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ZoneInitJob implements Job {

    @Value("${api.domain}")
    private String apiDomain;

    @Value("${api.url.zoneSearch}")
    private String zoneSearchUrl;

    @Autowired
    private AreaEntityRepository areaEntityRepository;

    @Autowired
    private ZoneEntityRepository zoneEntityRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("ZoneInitJob 실행 시작");

        try {
            List<AreaEntity> areaEntityList = areaEntityRepository.findAll();
            List<ZoneEntity> zoneEntityList = new ArrayList<>();

            for (AreaEntity areaEntity : areaEntityList) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiDomain + zoneSearchUrl)
                        .queryParam("areaCd", areaEntity.getAreaCd())
                        .queryParam("uname", "");

                RestTemplate restTemplate = new RestTemplate();

                String response = restTemplate.getForObject(builder.toUriString(), String.class);
                JAXBContext jaxbContext = JAXBContext.newInstance(SearchZone.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                SearchZone searchZone = (SearchZone) unmarshaller.unmarshal(new StringReader(response));

                List<SearchZone.ZoneCd> zoneCdList = searchZone.getZoneCdList();

                for (SearchZone.ZoneCd zoneCd : zoneCdList) {
                    ZoneEntity zoneEntity = ZoneEntity.from(zoneCd.getUCODE(), zoneCd.getUNAME(), zoneCd.getLAW_CD(), zoneCd.getLAW_NM());
                    zoneEntityList.add(zoneEntity);
                }
            }

            zoneEntityRepository.saveAll(zoneEntityList);

            System.out.println("데이터베이스에 데이터 저장 완료");

        } catch (Exception e) {
            System.err.println("ZoneInitJob 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new JobExecutionException(e);
        }

        System.out.println("ZoneInitJob 실행 완료");
    }

}
