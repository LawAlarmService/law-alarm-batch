package com.law.alarm.batch.sample.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "zone")
public class ZoneEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "ucode", length = 20)
    private String ucode;

    @Column(name = "uname", length = 20)
    private String uname;

    @Column(name = "law_cd", length = 20)
    private String lawCd;

    @Column(name = "law_nm", length = 20)
    private String lawNm;

    protected ZoneEntity() {
    }

    private ZoneEntity(String ucode, String uname, String lawCd, String lawNm) {
        this.ucode = ucode;
        this.uname = uname;
        this.lawCd = lawCd;
        this.lawNm = lawNm;
    }

    public static ZoneEntity from(String ucode, String uname, String lawCd, String lawNm) {
        return new ZoneEntity(ucode, uname, lawCd, lawNm);
    }

    @Override
    public String toString() {
        return "ZoneEntity{" +
                "zoneId=" + zoneId +
                ", ucode='" + ucode + '\'' +
                ", uname='" + uname + '\'' +
                ", lawCd='" + lawCd + '\'' +
                ", lawNm='" + lawNm + '\'' +
                '}';
    }

}
