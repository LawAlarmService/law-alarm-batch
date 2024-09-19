package com.law.alarm.batch.sample.dto.apiResponse;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "response")
public class SearchZone {

    private int totalSize;
    private List<ZoneCd> zoneCdList;

    @XmlElement(name = "totalSize")
    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    @XmlElement(name = "ZoneCd")
    public List<ZoneCd> getZoneCdList() {
        return zoneCdList;
    }

    public void setZoneCdList(List<ZoneCd> zoneCdList) {
        this.zoneCdList = zoneCdList;
    }

    @XmlType(propOrder = { "UCODE", "UNAME", "LAW_CD", "LAW_NM" })
    public static class ZoneCd {
        private String UCODE;
        private String UNAME;
        private String LAW_CD;
        private String LAW_NM;

        @XmlElement(name = "UCODE")
        public String getUCODE() {
            return UCODE;
        }

        public void setUCODE(String UCODE) {
            this.UCODE = UCODE;
        }

        @XmlElement(name = "UNAME")
        public String getUNAME() {
            return UNAME;
        }

        public void setUNAME(String UNAME) {
            this.UNAME = UNAME;
        }

        @XmlElement(name = "LAW_CD")
        public String getLAW_CD() {
            return LAW_CD;
        }

        public void setLAW_CD(String LAW_CD) {
            this.LAW_CD = LAW_CD;
        }

        @XmlElement(name = "LAW_NM")
        public String getLAW_NM() {
            return LAW_NM;
        }

        public void setLAW_NM(String LAW_NM) {
            this.LAW_NM = LAW_NM;
        }

    }

}
