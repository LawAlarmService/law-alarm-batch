package com.law.alarm.batch.sample.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "area"/*, schema = "law_alarm_service_db"*/)
public class AreaEntity {

    @Id
    @Column(name = "area_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_cd", nullable = false)
    private String areaCd;

    @Column(name = "area_city", nullable = false)
    private String areaCity;

    @Column(name = "area_district")
    private String areaDistrict;

    public Long getId() {
        return id;
    }

    public String getAreaCd() {
        return areaCd;
    }

    public String getAreaCity() {
        return areaCity;
    }

    public String getAreaDistrict() {
        return areaDistrict;
    }

    public String getData() {
        return id + " / " + areaCd + " / " + areaCity + " / " + areaDistrict;
    }

}
