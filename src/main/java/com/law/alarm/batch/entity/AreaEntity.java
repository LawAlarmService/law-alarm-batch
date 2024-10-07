package com.law.alarm.batch.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "area")
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

    @Override
    public String toString() {
        return "AreaEntity{" +
                "id=" + id +
                ", areaCd='" + areaCd + '\'' +
                ", areaCity='" + areaCity + '\'' +
                ", areaDistrict='" + areaDistrict + '\'' +
                '}';
    }

}
